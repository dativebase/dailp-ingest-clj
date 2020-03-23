(ns dailp-ingest-clj.tags
  "Ingests the DAILP Tags defined in the same-named Google Sheet at
  https://docs.google.com/spreadsheets/d/1eEk3JP2WTkP8BBShBHURrripKPredy-sCutQMiGfVmo/edit#gid=0."
  (:require [clojure.string :as string]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [dailp-ingest-clj.utils :refer [seq-rets->ret
                                            apply-or-error
                                            err->>
                                            table->sec-of-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [create-resource-with-unique-attr]]
            [dailp-ingest-clj.utils :as u]
            [dailp-ingest-clj.specs :as specs]))

(def ingest-tag-namespace "dailp-ingest")

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
      (second (string/split tag-name #":" 2)))}))

(defn create-tag
  "Create a tag from tag-map. Update an existing tag if one already exists with
  the specified name. In all cases, return a 2-element attempt vector where the
  first element (in the success case) is the tag map."
  [state tag-map]
  (create-resource-with-unique-attr
   state
   tag-map
   :resource-name :tag
   :unique-attr :name))

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
  (seq-rets->ret (pmap (partial create-tag state) tags)))

(defn get-tag-key
  "Return a map key for the tag: usually its name as a keyword, but ingest tag
  is special."
  [tag]
  (let [tag-name (:name tag)]
    (if (string/starts-with? tag-name ingest-tag-namespace)
      :ingest-tag
      (-> tag-name u/clean-for-kw keyword))))

(defn tags-seq->map
  "Convert a seq of tag maps to a mapping from generated tag keys to tag maps."
  [tags-seq]
  (into {} (map (fn [t] [(get-tag-key t) t]) tags-seq)))

(defn update-state-tags
  "Update state map's :tags map with the tags in uploaded-tags-ret."
  [state uploaded-tags-ret]
  (let [current-tags (:tags state)]
    (u/just
     (assoc state ::specs/tags-map
            (merge current-tags (tags-seq->map uploaded-tags-ret))))))

(defn fetch-upload-tags
  "Fetch tags from GSheets, upload them to OLD, return state map with :tags submap
  updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-all-tags disable-cache)
       (apply-or-error (partial upload-tags state))
       (apply-or-error (partial update-state-tags state))))
