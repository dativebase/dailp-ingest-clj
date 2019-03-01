(ns dailp-ingest-clj.verbs
  (:require [clojure.string :as string]
            [old-client.models :as ocm :refer [form create-form]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            empty-str-or-nil->nil
                                            seq-rets->ret
                                            str->kw]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [upsert-resource]]))

(def df-1975-sheet-name "DF1975--Master")
(def df-1975-worksheet-name "DF1975--Master")
(def df-1975-max-col 119)
(def df-1975-max-row 2350)

(def root-keys
  [:all-entries-key
   :df1975-page-ref
   :root-morpheme-break
   :morpheme-gloss
   :root-translation-1
   :root-translation-2
   :root-translation-3
   :transitivity
   :udb-class])

(def prs-glot-grade-keys
  [:prs-ʔ-grade-asp-tag
   :prs-ʔ-grade-asp-morpheme-break
   :prs-ʔ-grade-ppp-tag
   :prs-ʔ-grade-ppp-morpheme-break
   :prs-ʔ-grade-pp-tag
   :prs-ʔ-grade-pp-morpheme-break
   :prs-ʔ-grade-mid-refl-tag
   :prs-ʔ-grade-mid-refl-morpheme-break
   :prs-ʔ-grade-mod-tag
   :prs-ʔ-grade-mod-morpheme-break
   :prs-ʔ-grade-surface-form
   :prs-ʔ-grade-numeric
   :prs-ʔ-grade-simple-phonetics
   :prs-ʔ-grade-syllabary
   :prs-ʔ-grade-translation-1
   :prs-ʔ-grade-translation-2
   :prs-ʔ-grade-translation-3])

(def prs-h-grade-keys
  [:prs-h-grade-asp-tag
   :prs-h-grade-asp-morpheme-break
   :prs-h-grade-ppp-tag
   :prs-h-grade-ppp-morpheme-break
   :prs-h-grade-pp-tag
   :prs-h-grade-pp-morpheme-break
   :prs-h-grade-mid-refl-tag
   :prs-h-grade-mid-refl-morpheme-break
   :prs-h-grade-mod-tag
   :prs-h-grade-mod-morpheme-break
   :prs-h-grade-surface-form
   :prs-h-grade-numeric
   :prs-h-grade-simple-phonetics
   :prs-h-grade-syllabary
   :prs-h-grade-translation-1
   :prs-h-grade-translation-2
   :prs-h-grade-translation-3])

(def impf-keys
  [:impf-asp-tag
   :impf-asp-morpheme-break
   :impf-ppp-tag
   :impf-ppp-morpheme-break
   :impf-pp-tag
   :impf-pp-morpheme-break
   :impf-mid-refl-tag
   :impf-mid-refl-morpheme-break
   :impf-mod-tag
   :impf-mod-morpheme-break
   :impf-surface-form
   :impf-numeric
   :impf-simple-phonetics
   :impf-syllabary
   :impf-translation-1
   :impf-translation-2
   :impf-translation-3])

(def pft-keys
  [:pft-asp-tag
   :pft-asp-morpheme-break
   :pft-ppp-tag
   :pft-ppp-morpheme-break
   :pft-pp-tag
   :pft-pp-morpheme-break
   :pft-mid-refl-tag
   :pft-mid-refl-morpheme-break
   :pft-mod-tag
   :pft-mod-morpheme-break
   :pft-surface-form
   :pft-numeric
   :pft-simple-phonetics
   :pft-syllabary
   :pft-translation-1
   :pft-translation-2
   :pft-translation-3])

(def impt-keys
  [:impt-asp-tag
   :impt-asp-morpheme-break
   :impt-ppp-tag-1
   :impt-ppp-morpheme-break-1
   :impt-ppp-tag-2
   :impt-ppp-morpheme-break-2
   :impt-pp-tag
   :impt-pp-morpheme-break
   :impt-mid-refl-tag
   :impt-mid-refl-morpheme-break
   :impt-mod-tag
   :impt-mod-morpheme-break
   :impt-surface-form
   :impt-numeric
   :impt-simple-phonetics
   :impt-syllabary
   :impt-translation-1
   :impt-translation-2
   :impt-translation-3])

(def inf-keys
  [:inf-asp-tag
   :inf-asp-morpheme-break
   :inf-ppp-tag
   :inf-ppp-morpheme-break
   :inf-pp-tag
   :inf-pp-morpheme-break
   :inf-mid-refl-tag
   :inf-mid-refl-morpheme-break
   :inf-mod-tag
   :inf-mod-morpheme-break
   :inf-surface-form
   :inf-numeric
   :inf-simple-phonetics
   :inf-syllabary
   :inf-translation-1
   :inf-translation-2
   :inf-translation-3])

(def df-1975-key-sets
  {:root root-keys
   :prs-glot-grade prs-glot-grade-keys
   :prs-h-grade prs-h-grade-keys
   :impf impf-keys
   :pft pft-keys
   :impt impt-keys
   :inf inf-keys})

(defn create-verb
  "Create a verb from verb-map. Update an existing verb if one already exists with
  the specified name. In all cases, return a 2-element attempt vector where the
  first element (in the success case) is the verb map."
  [state verb-map]
  (upsert-resource state verb-map :resource-name :form))

(defn clean-verb-header-row
  [verb-header-row]
  (->> verb-header-row (map (fn [v] (if (nil? v) v (str->kw v))))))

(defn remove-empty-rows
  "Remove all rows (seqs) that consist of only nils and/or empty strings."
  [rows]
  (->> rows
       (map (fn [row] (map empty-str-or-nil->nil row)))
       (filter #(not (every? nil? %)))))

(defn row->seq-of-row-maps
  "Given a row (seq of strings and/or nils), return a map from keys in
  isomorphic header row to vals in row."
  [state header-row row]
  (->> row
       (map #(do [%1 %2]) header-row)
       (into {})))

(defn table->seq-of-row-maps
  [state [meta-row header-row & rows]]
  (let [meta-row (clean-verb-header-row meta-row)
        header-row (clean-verb-header-row header-row)]
    (->> rows
         (take 10)
         remove-empty-rows
         (reduce (fn [agg row]
                   (conj agg (row->seq-of-row-maps state header-row row)))
                 ()))))

(defn row-map->dailp-form-maps
  "Given a row map (from a GSheet extraction), return a DAILP form map, i.e.,
  a map whose key/value pairs are from the GSheet and are largely unmodified."
  [state row-map]
  (let [ret (map (fn [[verb-type keys]]
                   (assoc (select-keys row-map keys) :verb-type verb-type))
                 df-1975-key-sets)
        root-map (->> ret (filter (fn [m] (= (:verb-type m) :root))) first)]
    (map (fn [m] (assoc m :root root-map)) ret)))

(defmulti dailp-form-map->form-map
  (fn [state dailp-form-map] (:verb-type dailp-form-map)))

(defn get-translations
  [dailp-form-map translation-keys]
  (->> translation-keys
       (map (fn [k]
              (if-let [v (-> k dailp-form-map empty-str-or-nil->nil)]
                {::ocm/transcription v ::ocm/grammaticality ""}
                nil)))
       (filter identity)))

(def root-translation-keys
  [:root-translation-1
   :root-translation-2
   :root-translation-3])

(def root-simple-comments-keys
  {:transitivity "Transitivity"
   :df1975-page-ref "DF 1975 page reference"
   :all-entries-key "All entries key"
   :udb-class "UDB Class"})

(def prs-glot-grade-translation-keys
  [:prs-ʔ-grade-translation-2
   :prs-ʔ-grade-translation-1
   :prs-ʔ-grade-translation-3])

(def prs-glot-grade-simple-comments-keys
  {})

(defmulti get-comments 
  (fn [dailp-form-map] (:verb-type dailp-form-map)))

(defn get-simple-field-value-comments
  [dailp-form-map simple-comments-keys]
  (string/join
   " "
   (map (fn [[attr field-name]]
          (format "%s: %s." field-name (attr dailp-form-map)))
        simple-comments-keys)))

(defmethod get-comments :root
  [dailp-form-map]
  (get-simple-field-value-comments dailp-form-map root-simple-comments-keys))

(defmethod get-comments :prs-glot-grade
  [dailp-form-map]
  (format
   "Feeling 1975:%s (%s); narrow phonetic transcription source: Uchihara DB."
   (get-in dailp-form-map [:root :df1975-page-ref])
   (:prs-ʔ-grade-numeric dailp-form-map)))

(defmethod dailp-form-map->form-map :root
  [state dailp-form-map]
  (create-form
   {::ocm/transcription (:root-morpheme-break dailp-form-map)
    ::ocm/morpheme_break (:root-morpheme-break dailp-form-map)
    ::ocm/morpheme_gloss (:morpheme-gloss dailp-form-map)
    ::ocm/translations (get-translations dailp-form-map root-translation-keys)
    ::ocm/syntactic_category (get-in state [:syntactic-categories :V :id])
    ::ocm/comments (get-comments dailp-form-map)
    ::ocm/tags [(get-in state [:tags :ingest-tag :id])]}))

(defn compute-morpheme-break-gloss
  "Compute and return as a 2-vec the morpheme break (mb) and morpheme gloss (mb)
  for a DAILP DF 1975 surface form."
  [dailp-form-map getter-vecs]
  (->> getter-vecs
       (map (fn [key-path] (get-in dailp-form-map key-path)))
       (partition 2)
       (filter (fn [toople] (not (some (zipmap [nil] (repeat true)) toople))))
       (apply (partial map (fn [& args] (string/join "-" args))))))

;; Sorted vec of get-in vecs for getting the break/gloss 2-tuples for building
;; the morpheme break and morpheme gloss values of a prs-glot-grade surface form.
(def prs-glot-grade-mb-mg-getter-vecs
  [[:prs-ʔ-grade-ppp-morpheme-break]
   [:prs-ʔ-grade-ppp-tag]
   [:prs-ʔ-grade-pp-morpheme-break]
   [:prs-ʔ-grade-pp-tag]
   [:prs-ʔ-grade-mid-refl-morpheme-break]
   [:prs-ʔ-grade-mid-refl-tag]
   [:root :root-morpheme-break]
   [:root :morpheme-gloss]
   [:prs-ʔ-grade-asp-morpheme-break]
   [:prs-ʔ-grade-asp-tag]
   [:prs-ʔ-grade-mod-morpheme-break]
   [:prs-ʔ-grade-mod-tag]])

(defn get-kwixer
  "Given a prefix keyword, return a function that will prefix that keyword to
  any keyword passed into it. Keyword-prefixer = kwixer obvs."
  [prefix-kw]
  (fn [suffix-kw]
    (keyword (string/join "-" (map name (list prefix-kw suffix-kw))))))

(defn dailp-surface-form-map->form-map
  "Return an OLD form map constructed primarily from the DAILP form map
  dailp-form-map. the mb-mg-getter-vecs are a vector of vectors for getting
  the morpheme break/gloss values for the target form type. The translation
  keys are similarly a vector of keys for getting the translations of the
  target form type. The kwixer is a function that converts generic keyword
  keys like :surface-form to type-specific keys like :impt-surface-form."
  [state dailp-form-map mb-mg-getter-vecs translation-keys kwixer]
  (let [[mb mg]
        (compute-morpheme-break-gloss dailp-form-map mb-mg-getter-vecs)]
    (create-form
     {::ocm/narrow_phonetic_transcription ((kwixer :surface-form) dailp-form-map)
      ::ocm/phonetic_transcription ((kwixer :simple-phonetics) dailp-form-map)
      ::ocm/transcription ((kwixer :syllabary) dailp-form-map)
      ::ocm/morpheme_break mb
      ::ocm/morpheme_gloss mg
      ::ocm/translations (get-translations dailp-form-map translation-keys)
      ::ocm/syntactic_category (get-in state [:syntactic-categories :S :id])
      ::ocm/comments (get-comments dailp-form-map)
      ::ocm/tags [(get-in state [:tags :ingest-tag :id])]})))

(defmethod dailp-form-map->form-map :prs-glot-grade
  [state dailp-form-map]
  (dailp-surface-form-map->form-map
   state dailp-form-map
   prs-glot-grade-mb-mg-getter-vecs
   prs-glot-grade-translation-keys
   (get-kwixer :prs-ʔ-grade)))

(defmethod dailp-form-map->form-map :prs-h-grade
  [state dailp-form-map]
  [dailp-form-map :prs-h-grade])

(defmethod dailp-form-map->form-map :impf
  [state dailp-form-map]
  [dailp-form-map :impf])

(defmethod dailp-form-map->form-map :pft
  [state dailp-form-map]
  [dailp-form-map :pft])

(defmethod dailp-form-map->form-map :impt
  [state dailp-form-map]
  [dailp-form-map :impt])

(defmethod dailp-form-map->form-map :inf
  [state dailp-form-map]
  [dailp-form-map :inf])

(defn dailp-form-maps->form-maps
  [state dailp-form-maps]
  (map (partial dailp-form-map->form-map state) dailp-form-maps))

(defn row-map->form-maps
  [state row-map]
  (->> row-map
       (row-map->dailp-form-maps state)
       (dailp-form-maps->form-maps state)
  ))

(defn row-maps->form-maps
  [state row-maps]
  (reduce (fn [agg row-map] (concat agg (row-map->form-maps state row-map)))
          ()
          row-maps))

(defn table->seq-of-verb-form-maps
  [state table]
  (->> table
       (table->seq-of-row-maps state)
       (row-maps->form-maps state)
  )
)

(defn fetch-verbs-from-worksheet
  "Fetch the verbs from the Google Sheets worksheet."
  [disable-cache sheet-name worksheet-name max-col max-row]
  [(->> (fetch-worksheet-caching {:spreadsheet-title sheet-name
                                  :worksheet-title worksheet-name
                                  :max-col max-col
                                  :max-row max-row}
                                 disable-cache)) nil])

(defn upload-verbs 
  "Upload the seq of verb form resource maps to an OLD instance."
  [state verbs]
  (seq-rets->ret (map (partial create-verb state) verbs)))

(defn verbs-seq->map
  "Convert a seq of verb form maps to a mapping from int ids to form maps."
  [verbs-seq]
  (->> verbs-seq
       (map (fn [v] [(:id v) v]))
       (into {})))

;; (into {} (map (fn [v] [(:id v) v]) verbs-seq))

(defn update-state-verbs
  "Update state map's verbs-type-key map with the verbs in uploaded-verbs-ret."
  [state uploaded-verbs-ret verbs-type-key]
  (let [current-verbs (verbs-type-key state)]
    [(assoc state verbs-type-key
            (merge current-verbs (verbs-seq->map uploaded-verbs-ret))) nil]))

(defn fetch-upload-verbs
  "Fetch verbs from GSheets, upload them to OLD, return state map with verbs-type-key submap
  updated."
  ([state] (fetch-upload-verbs state true))
  ([state disable-cache]
   (->> (fetch-verbs-from-worksheet disable-cache
                                    df-1975-sheet-name
                                    df-1975-worksheet-name
                                    df-1975-max-col
                                    df-1975-max-row)
        (apply-or-error (partial table->seq-of-verb-form-maps state))
        ;; (apply-or-error (partial upload-verbs state))
        ;; (apply-or-error (partial update-state-verbs state))
        )))
