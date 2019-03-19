(ns dailp-ingest-clj.verbs-df-1975
  "Logic for ingesting DAILP verbs from the Google Sheets DF1975--Master:
  https://docs.google.com/spreadsheets/d/11ssqdimOQc_hp3Zk8Y55m6DFfKR96OOpclUg5wcGSVE/edit?usp=sharing."
  (:require [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.models :as ocm :refer [form create-form]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            empty-str-or-nil->nil
                                            seq-rets->ret
                                            str->kw]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [create-resource-either]]
            [dailp-ingest-clj.verbs :refer :all]))

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

(defn row-map->seq-of-form-maps
  [row-map]
  (map (fn [[verb-type keys]]
         (assoc (select-keys row-map keys) :verb-type verb-type))
       df-1975-key-sets))

(defn row-map->dailp-form-maps
  "Given a row map (from a GSheet extraction), return a DAILP form map, i.e.,
  a map whose key/value pairs are from the GSheet and are largely unmodified."
  [state row-map]
  (let [seq-of-form-maps (row-map->seq-of-form-maps row-map)]
    (map (fn [m] (assoc m :root (get-root-map seq-of-form-maps)))
         seq-of-form-maps)))

(defmulti dailp-form-map->form-map
  (fn [state dailp-form-map] (:verb-type dailp-form-map)))

(def root-simple-comments-keys
  {:transitivity "Transitivity"
   :df1975-page-ref "DF 1975 page reference"
   :all-entries-key "All entries key"
   :udb-class "UDB Class"})

(defmulti get-comments
  (fn [dailp-form-map _] (:verb-type dailp-form-map)))

(defmethod get-comments :root
  [dailp-form-map kwixer]
  (get-simple-field-value-comments dailp-form-map root-simple-comments-keys))

(defn get-feeling-numeric-comments
  "Return standard verb form comments: a Feeling 1975 reference and the
  transcription of the form in the numeric alphabet/orthography."
  [dailp-form-map kwixer]
  (format
   "Feeling 1975:%s (%s); narrow phonetic transcription source: Uchihara DB."
   (get-in dailp-form-map [:root :df1975-page-ref])
   (-> :numeric kwixer dailp-form-map)))

(defmethod get-comments :default
  [dailp-form-map kwixer]
  (get-feeling-numeric-comments dailp-form-map kwixer))

(defmethod dailp-form-map->form-map :root
  [state dailp-form-map]
  (let [translations (get-translations dailp-form-map root-translation-keys)
        form
        (create-form
         {::ocm/transcription (:root-morpheme-break dailp-form-map)
          ::ocm/morpheme_break (:root-morpheme-break dailp-form-map)
          ::ocm/morpheme_gloss (get-morpheme-gloss dailp-form-map translations)
          ::ocm/translations translations
          ::ocm/syntactic_category (get-in state [:syntactic-categories :V :id])
          ::ocm/comments (get-comments dailp-form-map nil)
          ::ocm/tags [(get-in state [:tags :ingest-tag :id])]})
        err (second form)]
    (if err
      [nil {:err err
            :inflection :root
            :all-entries-key (:all-entries-key dailp-form-map)
            :dailp-form-map dailp-form-map}]
      form)))

(defn get-standard-mb-mg-getter-vecs
  "Return a list of 'getter vectors' for the Cherokee verb:
  PPP-PP-MID/REFL-root-ASP-MOD."
  [kwixer inflection]
  (let [kwector (fn [k] (-> k kwixer vector))
        ppp-kws (if (= inflection :impt)
                  [:ppp-morpheme-break-1 :ppp-tag-1 :ppp-morpheme-break-2 :ppp-tag-2]
                  [:ppp-morpheme-break :ppp-tag])]
    (concat
     (map kwector ppp-kws)
     (map kwector [:pp-morpheme-break
                   :pp-tag
                   :mid-refl-morpheme-break
                   :mid-refl-tag])
     [[:root :root-morpheme-break]
      [:root :morpheme-gloss]]
     (map kwector [:asp-morpheme-break
                   :asp-tag
                   :mod-morpheme-break
                   :mod-tag]))))

(defn dailp-surface-form-map->form-map
  "Return an OLD form map constructed primarily from the DAILP form map
  dailp-form-map. The inflection param is a keyword like :impf or
  :prs-h-grade that identifies the grammatical inflection of the surface
  verb form."
  [state dailp-form-map inflection]
  (let [kwixer (get-kwixer inflection)
        [mb mg] (compute-morpheme-break-gloss
                 dailp-form-map (get-standard-mb-mg-getter-vecs
                                 kwixer inflection))
        mb (or mb "")
        mg (or mg "")
        form
        (create-form
         {::ocm/narrow_phonetic_transcription
          (or ((kwixer :surface-form) dailp-form-map) "")
          ::ocm/phonetic_transcription
          (or ((kwixer :simple-phonetics) dailp-form-map) "")
          ::ocm/transcription (or ((kwixer :syllabary) dailp-form-map) mb)
          ::ocm/morpheme_break mb
          ::ocm/morpheme_gloss mg
          ::ocm/translations (get-translations dailp-form-map
                                               (get-translation-keys kwixer))
          ::ocm/syntactic_category (get-in state [:syntactic-categories :S :id])
          ::ocm/comments (get-comments dailp-form-map kwixer)
          ::ocm/tags [(get-in state [:tags :ingest-tag :id])]})
        err (second form)]
    (if err
      [nil {:err err
            :all-entries-key (get-in dailp-form-map [:root :all-entries-key])
            :inflection inflection
            :dailp-form-map (succinct! dailp-form-map)}]
      form)))

(defmethod dailp-form-map->form-map :prs-glot-grade
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :prs-ʔ-grade))

(defmethod dailp-form-map->form-map :prs-h-grade
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :prs-h-grade))

(defmethod dailp-form-map->form-map :impf
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :impf))

(defmethod dailp-form-map->form-map :pft
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :pft))

(defmethod dailp-form-map->form-map :impt
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :impt))

(defmethod dailp-form-map->form-map :inf
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :inf))

(defn wrapped-dailp-form-map->form-map
  [state dailp-form-map]
  (try
    (dailp-form-map->form-map state dailp-form-map)
    (catch Exception e
      [nil
       {:exception e
        :dailp-form-map dailp-form-map}])))

(defn dailp-form-maps->form-maps
  [state dailp-form-maps]
  (map (partial wrapped-dailp-form-map->form-map state) dailp-form-maps))

(defn row-map-has-content?
  "Return true if at least one of the core attributes (as defined by
  df-1975-key-sets) of row-map has content."
  [row-map]
  (-> row-map
      :verb-type
      df-1975-key-sets
      (#(select-keys row-map %))
      vals
      ((partial filter identity))
      not-empty?))

(defn row-map-has-root-with-key?
  [row-map]
  (get-in row-map [:root :all-entries-key]))

(defn row-map-lacks-root-with-key?
  [row-map]
  (not (row-map-has-root-with-key? row-map)))

(defn remove-bad-dailp-form-maps
  [state dailp-form-maps]
  (->> dailp-form-maps
       (filter row-map-has-root-with-key?)
       (filter row-map-has-content?)))

(defn row-map->form-maps
  [state row-map]
  (->> row-map
       (row-map->dailp-form-maps state)
       (remove-bad-dailp-form-maps state)
       (dailp-form-maps->form-maps state)
  ))

(defn row-maps->form-maps
  [state row-maps]
  (reduce (fn [agg row-map]
            (concat agg (row-map->form-maps state row-map)))
          ()
          row-maps))

(defn fix-key-less-row
  "Fix row by adding to it all of the root-targeting attr/vals that row lacks
  from last-full-row."
  [row last-full-row]
  (let [good-parts (->> (select-keys row root-keys)
                        (map (fn [[k v]] (when v [k v])))
                        (into {}))
        fixer (merge (select-keys last-full-row root-keys) good-parts)]
    (merge row fixer)))

(defn root-is-valid?
  "Return true if the root within the supplied row map is valid."
  [row]
  (and (:all-entries-key row)
       (:root-morpheme-break row)
       (->> (map (fn [k] (k row))
                 [:root-translation-1
                  :root-translation-2
                  :root-translation-3])
            (filter identity)
            seq)))

(defn project-roots
  "Give good root attr-vals to all rows that lack them. The last seen good root
  attr-vals are assumed to apply to the current row, if it lacks good root
  attr-vals of its own."
  [row-maps]
  (->> row-maps
       (reduce (fn [[last-full-row rows] row]
                 (let [valid? (root-is-valid? row)
                       row (if valid? row (fix-key-less-row row last-full-row))
                       last-full-row (if valid? row last-full-row)]
                   [last-full-row (conj rows row)]))
               [nil []])
       second))

(defn table->forms
  "Convert a table data structure (vec of vecs of strings) to a seq of OLD forms
  (maps). The input table is at (verbs-key state). The output seq of form maps
  will be re-stored at the same key of state. Any errors produced while
  generating the form maps will be stored as warnings under
  (get-in state [:warnings verbs-key])."
  [verbs-key state]
  [(->> state
        verbs-key
        (table->row-maps state)
        project-roots
        (row-maps->form-maps state)
        (group-by (fn [[val err]] (if err :warnings :verbs)))
        ((fn [r] (-> state
                     (assoc verbs-key (->> r :verbs (map first)))
                     (assoc-in [:warnings verbs-key]
                               (->> r :warnings (map second))))))) nil])

(defn fetch-upload-verbs-df-1975
  "Fetch verbs from Google Sheets, upload them to OLD, return state map with
  verbs-type-key submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (let [verbs-key :df-1975-verbs]
    (->> state
         (fetch-verbs-from-worksheet disable-cache
                                     df-1975-sheet-name
                                     df-1975-worksheet-name
                                     df-1975-max-col
                                     df-1975-max-row
                                     verbs-key)
         (apply-or-error (partial table->forms verbs-key))
         (apply-or-error (partial upload-verbs verbs-key))
         (apply-or-error (partial update-state-verbs state verbs-key)))))
