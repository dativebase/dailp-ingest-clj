(ns dailp-ingest-clj.prepronominal-prefixes
  "Logic for ingesting DAILP prepronominal prefixes."
  (:require 
            [old-client.resources :refer [create-resource update-resource fetch-resources]]
            [old-client.models :refer [form]]
            [old-client.utils :refer [json-parse]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            seq-rets->ret
                                            table->sec-of-maps]]
            [dailp-ingest-clj.affixes :refer [affix-map->seq-of-forms]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.resources :refer [upsert-resource]]
            [clojure.data.csv :as csv])
  (:use [slingshot.slingshot :only [throw+ try+]]))

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
  [(reduce
    (fn [agg ppp-affix-map]
      (let [[seq-of-forms new-state]
            (affix-map->seq-of-forms
             ppp-affix-map (:state agg) :syncatkey :PPP)]
        (-> (assoc agg :state new-state)
            (update :ppp-form-maps
                    (fn [old-form-maps] (concat old-form-maps seq-of-forms))))))
    {:state state
     :ppp-form-maps ()}
    ppps) nil])

(defn fetch-upload-ppp-forms-DEPRECATED
  "Fetch PPP forms from GSheets, upload them to OLD, return state map with
   :created_pronominal_prefixes submap updated."
  [state]
  (construct-ppp-form-maps :state state))

(defn update-ppp
  [state ppp-map]
  :frogs)

(defn create-ppp
  "Create a PPP form from ppp-map. Update an existing PPP form if one already
  exists with the specified name. In all cases, return a 2-element attempt
  vector where the first element (in the success case) is the ppp map."
  [state ppp-map]
  (try+
   [(create-resource (:old-client state) :form ppp-map) nil]
   (catch [:status 400] {:keys [body]}
     (if-let [name-error (-> body json-parse :errors :name)]
       (update-ppp state ppp-map)
       [nil (json-parse body)]))
   (catch Object err
     [nil (format
           (str
            "Unknown error when attempting to create a PPP form with"
            " transcription '%s' and translation '%s': %s.")
           (:transcription ppp-map)
           (-> ppp-map :translations first :transcription)
           err)])))

(defn create-ppp
  [state ppp-map]
  :created-ppp
  )

(defn upload-ppps 
  "Upload the seq of PPP form resource maps (ppps) to an OLD instance."
  [state ppps]
  (seq-rets->ret
   (map (fn [ppp] (upsert-resource state ppp :resource-name :form)) ppps)))

(defn fetch-upload-ppp-forms
  "Fetch PPP forms from GSheets, upload them to OLD, return state map with
   :created_pronominal_prefixes submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (let [ret-map
        (->> (fetch-ppps-from-worksheet :disable-cache disable-cache)
             (apply-or-error (partial construct-ppp-form-maps state)))]
    ;; (-> ret-map first :ppp-form-maps)
    (apply-or-error
     (partial upload-ppps (-> ret-map first :state))
     [(-> ret-map first :ppp-form-maps) nil])
     ;; (apply-or-error (partial update-state-ppp-forms state))

))
