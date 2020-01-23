(ns dailp-ingest-clj.speakers
  "Ingests the DAILP Speaker defined in the same-named Google Sheet at
  https://docs.google.com/spreadsheets/d/1EWZGzxdSScqHKBTnDq9uTdnV4EbiVd7t7L8NPyHwxa4."
  (:require [clj-time.core :as t]
            [clj-time.format :as f]
            [clojure.string :as str]
            [dailp-ingest-clj.google-io :as gio]
            [dailp-ingest-clj.resources :as rs]
            [dailp-ingest-clj.utils :as u]
            [old-client.resources :as ocr]))

(def speakers-sheet-name "DAILP Speakers")

(def speakers-worksheet-name "Sheet1")

(defn create-speaker
  "Create a speaker from speaker-map. Update an existing speaker if one already
  exists with the specified key. In all cases, return a 2-element attempt vector
  where the first element (in the success case) is the speaker map."
  [state speaker-map]
  (try (u/just (ocr/create-resource (:old-client state) :speaker
                                    (u/map-hyphens->underscores speaker-map)))
       (catch Exception e
         (u/nothing
          (format
           "Error when attempting to create resources using %s. Error: %s."
           speaker-map e)))))

(defn fetch-speakers-from-worksheet
  "Fetch the speakers from the Google Sheets worksheet."
  [disable-cache]
  (u/just
   (->> (gio/fetch-worksheet-caching
         {:spreadsheet-title speakers-sheet-name
          :worksheet-title speakers-worksheet-name
          :max-col 4
          :max-row 2}
         disable-cache)
        u/table->sec-of-maps)))

(defn fetch-all-speakers
  "Fetch the speakers from the GSheet."
  [{{disable-cache :disable-cache} :tmp :as state}]
  (u/just-then
   (fetch-speakers-from-worksheet disable-cache)
   (fn [rows] (assoc-in state [:tmp :speaker-rows] rows))))

(defn upload-speakers
  "Upload the seq of speaker resource maps to an OLD instance."
  [{{speakers :speaker-rows} :tmp :as state}]
  (u/just-then
   (u/maybes->maybe (pmap (partial create-speaker state) speakers))
   (fn [uploaded-speakers]
     (assoc-in state [:tmp :speaker-maps] uploaded-speakers))))

(defn get-speaker-key
  "Return a map key for the speaker: its key as a keyword."
  [speaker]
  (:id speaker))

(defn speakers-seq->map
  "Convert a seq of speaker maps to a mapping from generated speaker keys to speaker
  maps."
  [speakers-seq]
  (->> speakers-seq
       (map (fn [t] [(get-speaker-key t) t]))
       (into {})))

(defn update-state-with-speakers
  "Update state map's :speakers map with the speakers in uploaded-speakers-ret."
  [{{speakers :speaker-maps} :tmp :as state}]
  (u/just
   (-> state
       (assoc :speakers (speakers-seq->map speakers))
       (dissoc :tmp))))

(defn fetch-upload-speakers
  "Fetch speakers from GSheets, upload them to OLD, return state map with
  :speakers submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (u/err->> (assoc-in state [:tmp :disable-cache] disable-cache)
            fetch-all-speakers
            upload-speakers
            update-state-with-speakers))
