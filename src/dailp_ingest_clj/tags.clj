(ns dailp-ingest-clj.tags
  (:require [clojure.string :as string]
            [clj-time.core :as t]
            [old-client.resources :refer [create-resource update-resource fetch-resources]]
            [old-client.utils :refer [json-parse]]
            [clj-time.format :as f]
            [dailp-ingest-clj.utils :refer [seq-rets->ret
                                            apply-or-error
                                            csv-data->maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(def ingest-tag-namespace "ingest-uchihara-root")

(def tags-sheet-name "DAILP Tags")

(def tags-worksheet-name "Sheet1")

(defn get-now [] (t/now))

(defn get-now-iso8601
  []
  (f/unparse (f/formatters :basic-date-time) (get-now)))

(defn get-ingest-tag-name
  []
  (format "%s:%s" ingest-tag-namespace (get-now-iso8601)))

(defn get-ingest-tag-map
  []
  (let [tag-name (get-ingest-tag-name)]
    {:name tag-name
     :description
     (format
      "The tag for the data ingest that occurred at %s."
      (second (string/split tag-name #":")))}))

(defn update-tag
  "Update the tag matching tag-map by name. Return a 2-element
  attempt vector."
  [state tag-map]
  (let [existing-tag
        (fetch-resources (:old-client state) :tag)
        tag-to-update
        (first (filter #(= (:name tag-map) (:name %))
                       existing-tag))]
    (try+
     [(update-resource (:old-client state) :tag (:id tag-to-update)
                       tag-map)
      nil]
     (catch [:status 400] {:keys [body]}
       (if-let [error (-> body json-parse :error)]
         (if (= error (str "The update request failed because the submitted"
                           " data were not new."))
           [(format (str "Update not required for tag '%s': it already"
                         " exists in its desired state.")
                    (:name tag-map)) nil]
           [nil (format (str "Unexpected 'error' message when updating tag"
                             " '%s': '%s'.")
                        (:name tag-map)
                        (error))])
         [nil (format (str "Unexpected error updating tag '%s'. No ':error'"
                           " key in JSON body."))]))
     (catch Object err
       [nil (format
             "Unknown error when attempting to update tag named '%s': '%s'"
             (:name tag-map)
             err)]))))

(defn create-tag
  "Create a tag from tag-map. Update an existing tag if one already exists with
  the specified name. In all cases, return a 2-element attempt vector."
  [state tag-map]
  (try+
   (create-resource (:old-client state) :tag tag-map)
   [(format "Created tag '%s'." (:name tag-map)) nil]
   (catch [:status 400] {:keys [body]}
     (if-let [name-error (-> body json-parse :errors :name)]
       (update-tag state tag-map)
       [nil (json-parse body)]))
   (catch Object err
     [nil (format
           "Unknown error when attempting to create tag named '%s': '%s'."
           (:name tag-map)
           err)])))

(defn create-ingest-tag
  [state]
  (create-tag state (get-ingest-tag-map)))

(defn fetch-tags-from-worksheet
  "Fetch the tags from the Google Sheets worksheet."
  [disable-cache]
  [(->> (fetch-worksheet-caching {:spreadsheet tags-sheet-name
                                  :worksheet tags-worksheet-name}
                                 disable-cache)
        csv-data->maps) nil])

(defn fetch-all-tags
  [disable-cache]
  (let [fetched (fetch-tags-from-worksheet disable-cache)]
    [(conj (first fetched) (get-ingest-tag-map)) nil]))

(defn upload-tags 
  "Upload the seq of tag resource maps to an OLD instance."
  [state tags]
  (seq-rets->ret (map (partial create-tag state) tags)))

(defn fetch-upload-tags
  ([state] (fetch-upload-tags state true))
  ([state disable-cache]
   (apply-or-error
    (partial upload-tags state)
    (fetch-all-tags disable-cache))))
