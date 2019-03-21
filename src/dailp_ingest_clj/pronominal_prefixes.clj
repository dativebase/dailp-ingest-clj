(ns dailp-ingest-clj.pronominal-prefixes
  "Logic for ingesting DAILP pronominal prefixes from the Google Sheet at
  https://docs.google.com/spreadsheets/d/1OMzkbDGY1BqPR_ZwJRe4-F5_I12Ao5OJqqMp8Ej_ZhE/edit?usp=sharing."
  (:require [dailp-ingest-clj.affixes :refer [affix-map->seq-of-forms
                                              construct-affix-form-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [upsert-resource]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            seq-rets->ret
                                            table->sec-of-maps]]))

(def pps-sheet-name "Combined Pronominal Prefixes")

(def pps-worksheet-name "Sheet1")

(defn fetch-pps-from-worksheet
  "Fetch the pronominal prefixes from the Google Sheets worksheet."
  [& {:keys [disable-cache] :or {disable-cache true}}]
   [(->> (fetch-worksheet-caching {:spreadsheet-title pps-sheet-name
                                   :worksheet-title pps-worksheet-name
                                   :max-col 13
                                   :max-row 27}
                                  disable-cache)
         table->sec-of-maps) nil])

(defn construct-pp-form-maps
  [state pps]
  (let [ret
        (reduce
         (fn [agg pp-affix-map]
           (let [[seq-of-forms new-state]
                 (affix-map->seq-of-forms
                  pp-affix-map (:state agg) :syncatkey :PP)]
             (-> (assoc agg :state new-state)
                 (update :pp-form-maps
                         (fn [old-form-maps]
                           (concat old-form-maps seq-of-forms))))))
         {:state state :pp-form-maps ()}
         pps)
        ret (update ret :pp-form-maps seq-rets->ret)]
    (apply-or-error
     (fn [_] [(update ret :pp-form-maps first) nil])
     (:pp-form-maps ret))))

(defn construct-pp-form-maps
  "Return an either whose value is a map whose keys are :state and
  :pp-form-maps. The state value may have warnings added to it. The
  PP suffix value should be a seq of form maps representing pronominal
  prefixes."
  [state pps]
  (construct-affix-form-maps state pps :pp-form-maps :PP))

(defn upload-pps 
  "Upload the seq of PP form resource maps (pps) to an OLD instance."
  [{:keys [state pp-form-maps]}]
  (apply-or-error
   (fn [pp-forms] [{:state state :pp-forms pp-forms} nil])
   (seq-rets->ret
    (map (fn [pp] (upsert-resource state pp :resource-name :form))
         pp-form-maps))))

(defn update-state-pp-forms
  [{:keys [state pp-forms]}]
  [(assoc state :pp-forms
          (into {} (map (fn [f] [(:id f) f]) pp-forms))) nil])

(defn fetch-upload-pp-forms
  "Fetch PP forms from GSheets, upload them to OLD, return state map with
   :created_pronominal_prefixes submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-pps-from-worksheet :disable-cache disable-cache)
       (apply-or-error (partial construct-pp-form-maps state))
       (apply-or-error upload-pps)
       (apply-or-error update-state-pp-forms)))
