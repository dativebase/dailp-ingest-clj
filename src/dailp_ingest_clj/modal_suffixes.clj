(ns dailp-ingest-clj.modal-suffixes
  "Logic for ingesting DAILP modal suffixes."
  (:require [dailp-ingest-clj.affixes :refer [affix-map->seq-of-forms
                                              construct-affix-form-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [upsert-resource]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            seq-rets->ret
                                            table->sec-of-maps]]
            [dailp-ingest-clj.utils :as u]
            [dailp-ingest-clj.specs :as specs]))

(def mod-sfxs-sheet-name "Modal Suffixes")

(def mod-sfxs-worksheet-name "Sheet1")

(defn fetch-mod-sfxs-from-worksheet
  "Fetch the modal suffixes from the Google Sheets worksheet."
  [& {:keys [disable-cache] :or {disable-cache true}}]
   [(->> (fetch-worksheet-caching {:spreadsheet-title mod-sfxs-sheet-name
                                   :worksheet-title mod-sfxs-worksheet-name
                                   :max-col 17
                                   :max-row 10}
                                  disable-cache)
         table->sec-of-maps) nil])

(defn construct-mod-sfx-form-maps
  "Return an either whose value is a map whose keys are :state and
  :mod-sfx-form-maps. The state value may have warnings added to it. The
  MOD suffix value should be a seq of form maps representing modal suffixes."
  [state mod-sfxs]
  (construct-affix-form-maps state mod-sfxs :mod-sfx-form-maps :MOD))

(defn upload-mod-sfxs
  "Upload the seq of MOD form resource maps (mod-sfxs) to an OLD instance."
  [{:keys [state mod-sfx-form-maps]}]
  (apply-or-error
   (fn [mod-sfx-forms] [{:state state :mod-sfx-forms mod-sfx-forms} nil])
   (seq-rets->ret
    (map (fn [mod-sfx] (upsert-resource state mod-sfx :resource-name :form))
         mod-sfx-form-maps))))

(defn update-state-mod-sfx-forms
  [{:keys [state mod-sfx-forms]}]
  (u/just
   (update
    state
    ::specs/forms-map
    merge
    (->> mod-sfx-forms
         (map (fn [f] [(:id f) f]))
         (into {})))))

(defn fetch-upload-mod-sfx-forms
  "Fetch MOD forms from GSheets, upload them to OLD, return state map with
   :created_pronominal_prefixes submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-mod-sfxs-from-worksheet :disable-cache disable-cache)
       (apply-or-error (partial construct-mod-sfx-form-maps state))
       (apply-or-error upload-mod-sfxs)
       (apply-or-error update-state-mod-sfx-forms)))
