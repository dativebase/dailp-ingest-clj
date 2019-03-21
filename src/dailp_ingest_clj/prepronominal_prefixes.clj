(ns dailp-ingest-clj.prepronominal-prefixes
  "Logic for ingesting DAILP prepronominal prefixes from the Google Sheet at
  https://docs.google.com/spreadsheets/d/12v5fqtOztwwLeEaKQJGMfziwlxP4n60riMsN9dYw9Xc/edit#gid=0."
  (:require [dailp-ingest-clj.affixes :refer [affix-map->seq-of-forms
                                              construct-affix-form-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [upsert-resource]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            seq-rets->ret
                                            table->sec-of-maps]]))

(def ppps-sheet-name "Prepronominal prefixes")

(def ppps-worksheet-name "Sheet1")

(defn fetch-ppps-from-worksheet
  "Fetch the syntactic categories from the Google Sheets worksheet."
  [& {:keys [disable-cache] :or {disable-cache true}}]
   [(->> (fetch-worksheet-caching {:spreadsheet-title ppps-sheet-name
                                   :worksheet-title ppps-worksheet-name
                                   :max-col 31
                                   :max-row 15}
                                  disable-cache)
         table->sec-of-maps) nil])

(defn construct-ppp-form-maps
  [state ppps]
  (let [ret
        (reduce
         (fn [agg ppp-affix-map]
           (let [[seq-of-forms new-state]
                 (affix-map->seq-of-forms
                  ppp-affix-map (:state agg) :syncatkey :PPP)]
             (-> (assoc agg :state new-state)
                 (update :ppp-form-maps
                         (fn [old-form-maps]
                           (concat old-form-maps seq-of-forms))))))
         {:state state :ppp-form-maps ()}
         ppps)
        ret (update ret :ppp-form-maps seq-rets->ret)]
    (apply-or-error
     (fn [_] [(update ret :ppp-form-maps first) nil])
     (:ppp-form-maps ret))))

(defn construct-ppp-form-maps
  "Return an either whose value is a map whose keys are :state and
  :ppp-form-maps. The state value may have warnings added to it. The
  PPP suffix value should be a seq of form maps representing prepronominal
  suffixes."
  [state ppps]
  (construct-affix-form-maps state ppps :ppp-form-maps :PPP))

(defn upload-ppps 
  "Upload the seq of PPP form resource maps (ppps) to an OLD instance."
  [{:keys [state ppp-form-maps]}]
  (apply-or-error
   (fn [ppp-forms] [{:state state :ppp-forms ppp-forms} nil])
   (seq-rets->ret
    (map (fn [ppp] (upsert-resource state ppp :resource-name :form))
         ppp-form-maps))))

(defn update-state-ppp-forms
  [{:keys [state ppp-forms]}]
  [(assoc state :ppp-forms
          (into {} (map (fn [f] [(:id f) f]) ppp-forms))) nil])

(defn fetch-upload-ppp-forms
  "Fetch PPP forms from GSheets, upload them to OLD, return state map with
   :created_pronominal_prefixes submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-ppps-from-worksheet :disable-cache disable-cache)
       (apply-or-error (partial construct-ppp-form-maps state))
       (apply-or-error upload-ppps)
       (apply-or-error update-state-ppp-forms)))
