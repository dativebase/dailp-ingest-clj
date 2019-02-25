(ns dailp-ingest-clj.tags
  (:require [clojure.string :as string]
            [clj-time.core :as t]
            [old-client.resources :refer [create-resource update-resource fetch-resources]]
            [old-client.utils :refer [json-parse]]
            [clj-time.format :as f]
            [dailp-ingest-clj.utils :refer [seq-rets->ret
                                            apply-or-error
                                            table->sec-of-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(def ingest-tag-namespace "ingest-uchihara-root")

(def tags-sheet-name "DAILP Tags")

(def tags-worksheet-name "Sheet1")

(defn get-now [] (t/now))

(defn get-now-iso8601
  []
  (f/unparse (f/formatters :date-time) (get-now)))

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
  attempt vector where the first element (in the success case) is the tag map."
  [state tag-map]
  (let [existing-tags
        (fetch-resources (:old-client state) :tag)
        tag-to-update
        (first (filter #(= (:name tag-map) (:name %))
                       existing-tags))]
    (try+
     [(update-resource (:old-client state) :tag (:id tag-to-update)
                       tag-map)
      nil]
     (catch [:status 400] {:keys [body]}
       (if-let [error (-> body json-parse :error)]
         (if (= error (str "The update request failed because the submitted"
                           " data were not new."))
           [tag-to-update nil]
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
  the specified name. In all cases, return a 2-element attempt vector where the
  first element (in the success case) is the tag map."
  [state tag-map]
  (try+
   [(create-resource (:old-client state) :tag tag-map) nil]
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
  "Create the unique tag for this ingest. Its name contains a timestamp."
  [state]
  (create-tag state (get-ingest-tag-map)))

(defn fetch-tags-from-worksheet
  "Fetch the tags from the Google Sheets worksheet."
  [disable-cache]
  [(->> (fetch-worksheet-caching {:spreadsheet-title tags-sheet-name
                                  :worksheet-title tags-worksheet-name
                                  :max-col 2
                                  :max-row 17}
                                 disable-cache)
        table->sec-of-maps) nil])

(defn fetch-all-tags
  "Fetch the tags from the GSheet and also the ingest tag."
  [disable-cache]
  (let [fetched (fetch-tags-from-worksheet disable-cache)]
    [(conj (first fetched) (get-ingest-tag-map)) nil]))

(defn upload-tags 
  "Upload the seq of tag resource maps to an OLD instance."
  [state tags]
  (seq-rets->ret (map (partial create-tag state) tags)))

(defn get-tag-key
  "Return a map key for the tag: usually its name as a keyword, but ingest tag
  is special."
  [tag]
  (let [tag-name (:name tag)]
    (if (string/starts-with? tag-name ingest-tag-namespace)
      :ingest-tag (keyword tag-name))))

(defn tags-seq->map
  "Convert a seq of tag maps to a mapping from generated tag keys to tag maps."
  [tags-seq]
  (into {} (map (fn [t] [(get-tag-key t) t]) tags-seq)))

(defn update-state-tags
  "Update state map's :tags map with the tags in uploaded-tags-ret."
  [state uploaded-tags-ret]
  (let [current-tags (:tags state)]
    (assoc state :tags (merge current-tags (tags-seq->map uploaded-tags-ret)))))

(defn fetch-upload-tags
  "Fetch tags from GSheets, upload them to OLD, return state map with :tags submap
  updated."
  ([state] (fetch-upload-tags state true))
  ([state disable-cache]
   (->> (fetch-all-tags disable-cache)
        (apply-or-error (partial upload-tags state))
        (apply-or-error (partial update-state-tags state)))))
