(ns dailp-ingest-clj.aspectual-suffixes
  "Logic for ingesting DAILP aspectual suffixes."
  (:require [old-client.models :as ocm :refer [form create-form]]
            [dailp-ingest-clj.affixes :refer [affix-map->seq-of-forms
                                              construct-affix-form-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [upsert-resource]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            seq-rets->ret
                                            table->sec-of-maps]]
            [dailp-ingest-clj.utils :as u]
            [dailp-ingest-clj.specs :as specs]))

(def asp-sfxs-sheet-name "Aspectual Suffixes")

(def asp-sfxs-worksheet-name "Sheet1")

(def tmp [["Present Aspectual Suffix"
           "Imperfective Aspectual Suffix"
           "Perfective Aspectual Suffix"
           "Punctual Aspectual Suffix"
           "Infinitive Aspectual Suffix"]
          ["PRS" "IMPF" "PFT" "PCT" "INF"]
          [":h" ":h" ":s" ":h" ":hist"]
          ["aʔ" ":s" ":ʔn" ":l" ":hlist"]])

(def intermediate-tmp
  {:translations
   ["Present Aspectual Suffix"
    "Imperfective Aspectual Suffix"
    "Perfective Aspectual Suffix"
    "Punctual Aspectual Suffix"
    "Infinitive Aspectual Suffix"],
   :glosses ["PRS" "IMPF" "PFT" "PCT" "INF"],
   :rows (list [":h" ":h" ":s" ":h" ":hist"] ["aʔ" ":s" ":ʔn" ":l" ":hlist"])})

(defn triple->form-map
  "Given a triple of [translation gloss form] return an OLD form map."
  [state syncatkey [translation gloss form]]
  (if (nil? form)
    nil
    (create-form {::ocm/transcription form
                  ::ocm/morpheme_break form
                  ::ocm/morpheme_gloss gloss
                  ::ocm/translations [{::ocm/transcription translation
                                       ::ocm/grammaticality ""}]
                  ::ocm/syntactic_category
                  (get-in state [:syntactic-categories syncatkey :id])
                  ::ocm/tags [(get-in state [::specs/tags-map :ingest-tag :id])]})))

(defn asp-table->sec-of-maps
  "Given an Aspectual affix table (vec of vecs), return an OLD form map. Note:
  all columns have the gloss/translation that is defined by the top two rows."
  [state [translations glosses & rows]]
  (let [ret (->> (mapcat (fn [tr-vec gl-vec row]
                           (map (fn [tr gl mb] [tr gl mb])
                                tr-vec gl-vec row))
                         (repeat translations)
                         (repeat glosses)
                         rows)
                 (map (partial triple->form-map state :ASP))
                 (filter identity)  ;; remove nils
                 seq-rets->ret)]
    (apply-or-error
     (fn [r] [{:state state :asp-sfx-forms r} nil])
     ret)))

(defn fetch-asp-sfxs-from-worksheet
  "Fetch the aspectual suffixes from the Google Sheets worksheet."
  [& {:keys [disable-cache] :or {disable-cache true}}]
  [(fetch-worksheet-caching {:spreadsheet-title asp-sfxs-sheet-name
                             :worksheet-title asp-sfxs-worksheet-name
                             :max-col 5
                             :max-row 71}
                            disable-cache) nil])

(defn upload-asp-sfxs 
  "Upload the seq of ASP form resource maps (asp-sfxs) to an OLD instance."
  [{:keys [state asp-sfx-forms]}]
  (apply-or-error
   (fn [asp-sfx-forms] [{:state state :asp-sfx-forms asp-sfx-forms} nil])
   (seq-rets->ret
    (map (fn [asp-sfx] (upsert-resource state asp-sfx :resource-name :form))
         asp-sfx-forms))))

(defn update-state-asp-sfx-forms
  [{:keys [state asp-sfx-forms]}]
  (u/just
   (update
    state
    ::specs/forms-map
    merge
    (->> asp-sfx-forms
         (map (fn [f] [(:id f) f]))
         (into {})))))

(defn fetch-upload-asp-sfx-forms
  "Fetch ASP forms from GSheets, upload them to OLD, return state map with
   :created_pronominal_prefixes submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-asp-sfxs-from-worksheet :disable-cache disable-cache)
       (apply-or-error (partial asp-table->sec-of-maps state))
       (apply-or-error upload-asp-sfxs)
       (apply-or-error update-state-asp-sfx-forms)))
