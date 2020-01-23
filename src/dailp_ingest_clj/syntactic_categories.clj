(ns dailp-ingest-clj.syntactic-categories
    "Logic for ingesting DAILP syntactic categories from the Google Sheet at
  https://docs.google.com/spreadsheets/d/159i_Cygdqsnp55QBzqJu7eozxsNEiVIiXhwEzls3q7g/edit?usp=sharing."
  (:require [old-client.models :refer [syntactic-category]]
            [dailp-ingest-clj.utils :refer [seq-rets->ret
                                            apply-or-error
                                            table->sec-of-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer
             [create-resource-with-unique-attr]]))

(def syntactic-categories-sheet-name "DAILP Syntactic Categories")

(def syntactic-categories-worksheet-name "Sheet1")

(defn create-syntactic-category
  "Create a syntactic category from syntactic-category-map. Update an existing
  syntactic category if one already exists with the specified name. In all
  cases, return a 2-element attempt vector."
  [state syntactic-category-map]
  (create-resource-with-unique-attr
   state
   syntactic-category-map
   :resource-name :syntactic-category
   :unique-attr :name))

(defn upload-syntactic-categories
  "Upload the seq of syntactic category resource maps to an OLD instance."
  [state syntactic-categories]
  (seq-rets->ret (map (partial create-syntactic-category state)
                      syntactic-categories)))

(defn fetch-syntactic-categories-from-worksheet
  "Fetch the syntactic categories from the Google Sheets worksheet."
  [disable-cache]
  [(->> (fetch-worksheet-caching {:spreadsheet-title syntactic-categories-sheet-name
                                  :worksheet-title syntactic-categories-worksheet-name
                                  :max-col 3
                                  :max-row 10}
                                 disable-cache)
       table->sec-of-maps
       (map (fn [sc] (merge syntactic-category sc)))) nil])

(defn get-sc-key
  "Return a map key for the syntactic category sc."
  [sc] (-> sc :name keyword))

(defn scs-seq->map
  [scs-seq]
  (into {} (map (fn [sc] [(get-sc-key sc) sc]) scs-seq)))

(defn update-state-scs
  "Update state map's :syntactic-categories map with the syntactic categories
  in uploaded-scs-ret."
  [state uploaded-scs-seq]
  [(assoc state :syntactic-categories (scs-seq->map uploaded-scs-seq)) nil])

(defn fetch-upload-syntactic-categories
  "Fetch the syntactic-categories from Google Sheets and upload them to an OLD
  instance. Should return a message string for each syntactic category upload
  attempt."
  ([state] (fetch-upload-syntactic-categories state true))
  ([state disable-cache]
   (->> (fetch-syntactic-categories-from-worksheet disable-cache)
        (apply-or-error (partial upload-syntactic-categories state))
        (apply-or-error (partial update-state-scs state)))))
