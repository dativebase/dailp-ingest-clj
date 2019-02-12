(ns dailp-ingest-clj.syntactic-categories
  "Logic for ingesting DAILP syntactic categories."
  (:require [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.resources :refer [create-resource update-resource fetch-resources]]
            [old-client.models :refer [syntactic-category]]
            [old-client.utils :refer [json-parse]]
            [dailp-ingest-clj.utils :refer [strip str->kw
                                            seq-rets->ret
                                            err->>
                                            apply-or-error
                                            csv-data->maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.old-io :refer [get-state]])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(def syntactic-categories-sheet-name "DAILP Syntactic Categories")

(def syntactic-categories-worksheet-name "Sheet1")

(defn update-syntactic-category
  "Update the syntactic category matching syntactic-category-map by name. Return a 2-element
  attempt vector."
  [state syntactic-category-map]
  (let [existing-syntactic-categories
        (fetch-resources (:old-client state) :syntactic-category)
        sc-to-update
        (first (filter #(= (:name syntactic-category-map) (:name %))
                       existing-syntactic-categories))]
    (try+
     [(update-resource (:old-client state) :syntactic-category (:id sc-to-update)
                       syntactic-category-map)
      nil]
     (catch [:status 400] {:keys [body]}
       (if-let [error (-> body json-parse :error)]
         (if (= error (str "The update request failed because the submitted"
                           " data were not new."))
           [(format (str "Update not required for syntactic category '%s': it already"
                         " exists in its desired state.")
                    (:name syntactic-category-map)) nil]
           [nil (format (str "Unexpected 'error' message when updating syntactic category"
                             " '%s': '%s'.")
                        (:name syntactic-category-map)
                        (error))])
         [nil (format (str "Unexpected error updating syntactic category '%s'. No ':error'"
                           " key in JSON body."))]))
     (catch Object err
       [nil (format
             "Unknown error when attempting to update syntactic category named '%s': '%s'"
             (:name syntactic-category-map)
             err)]))))

(defn create-syntactic-category
  "Create a syntactic category from syntactic-category-map. Update an existing
  syntactic category if one already exists with the specified name. In all
  cases, return a 2-element attempt vector."
  [state syntactic-category-map]
  (try+
   (create-resource (:old-client state) :syntactic-category syntactic-category-map)
   [(format "Created syntactic category '%s'." (:name syntactic-category-map)) nil]
   (catch [:status 400] {:keys [body]}
     (if-let [name-error (-> body json-parse :errors :name)]
       (update-syntactic-category state syntactic-category-map)
       [nil (json-parse body)]))
   (catch Object err
     [nil (format
           "Unknown error when attempting to create syntactic category named '%s': '%s'."
           (:name syntactic-category-map)
           err)])))

(defn upload-syntactic-categories 
  "Upload the seq of syntactic category resource maps to an OLD instance."
  [state syntactic-categories]
  (seq-rets->ret (map (partial create-syntactic-category state)
                      syntactic-categories)))

(defn fetch-syntactic-categories-from-worksheet
  "Fetch the syntactic categories from the Google Sheets worksheet."
  [disable-cache]
  [(->> (fetch-worksheet-caching {:spreadsheet syntactic-categories-sheet-name
                                  :worksheet syntactic-categories-worksheet-name}
                                 disable-cache)
       csv-data->maps
       (map (fn [sc] (merge syntactic-category sc)))) nil])

(defn fetch-upload-syntactic-categories
  "Fetch the syntactic-categories from Google Sheets and upload them to an OLD
  instance. Should return a message string for each syntactic category upload
  attempt."
  ([state] (fetch-upload-syntactic-categories state true))
  ([state disable-cache]
   (apply-or-error
    (partial upload-syntactic-categories state)
    (fetch-syntactic-categories-from-worksheet disable-cache))))
