(ns dailp-ingest-clj.sources
  "Ingests the DAILP Sources defined in the same-named Google Sheet at
  https://docs.google.com/spreadsheets/d/1W46XymhtohAizs_KVRCNvfTUL0k4-LWwYbEv8aHau_4."
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]
            [dailp-ingest-clj.google-io :as gio]
            [dailp-ingest-clj.old-io :as old-io]
            [dailp-ingest-clj.resources :as rs]
            [dailp-ingest-clj.specs :as specs]
            [dailp-ingest-clj.utils :as u]
            [old-client.core :as oc]
            [clojure.spec.alpha :as spec]))

(def sources-sheet-name "DAILP Sources")

(def sources-worksheet-name "Sheet1")

(defn create-source
  "Create a source from source-map. Update an existing source if one already
  exists with the specified key. In all cases, return a 2-element attempt vector
  where the first element (in the success case) is the source map."
  [state source-map]
  (rs/create-resource-with-unique-attr
   state
   source-map
   :resource-name :source
   :unique-attr :key))

(defn fetch-sources-from-worksheet
  "Fetch the sources from the Google Sheets worksheet."
  [disable-cache]
  (u/just
   (->> (gio/fetch-worksheet-caching
         {:spreadsheet-title sources-sheet-name
          :worksheet-title sources-worksheet-name
          :max-col 8
          :max-row 3}  ;; intentional: so we don't cite Uchihara on row 4
         disable-cache)
        u/table->sec-of-maps)))

(defn fetch-all-sources
  "Fetch the sources from the GSheet."
  [disable-cache]
  (fetch-sources-from-worksheet disable-cache))

(defn upload-sources 
  "Upload the seq of source resource maps to an OLD instance."
  [state sources]
  (u/seq-rets->ret (pmap (partial create-source state) sources)))

(defn get-source-key
  "Return a map key for the source: its key as a keyword."
  [source]
  (-> source :key keyword))

(defn sources-seq->map
  "Convert a seq of source maps to a mapping from generated source keys to source
  maps."
  [sources-seq]
  (->> sources-seq
       (map (fn [t] [(get-source-key t) t]))
       (into {})))

(defn update-state-sources
  "Update state map's :sources map with the sources in uploaded-sources-ret."
  [state uploaded-sources-ret]
  (u/just
   (update state
           ::specs/sources
           merge
           (sources-seq->map uploaded-sources-ret))))

(defn fetch-upload-sources
  "Fetch sources from GSheets, upload them to OLD, return state map with :sources
  submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-all-sources disable-cache)
       (u/apply-or-error (partial upload-sources state))
       (u/apply-or-error (partial update-state-sources state))))

(def url "http://127.0.0.1:61001/old")
(def username "admin")
(def password "adminA_1")

(comment

  (old-io/get-state (oc/make-old-client {:url url :username username :password password}))

  (fetch-upload-sources
   (old-io/get-state
    (oc/make-old-client
     {:url url
      :username username
      :password password})))

)
