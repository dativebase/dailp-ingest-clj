(ns dailp-ingest-clj.verbs-df-2003
  "Logic for ingesting DAILP verbs from the Google Sheet DF2003--Master:
  https://docs.google.com/spreadsheets/d/18cKXgsfmVhRZ2ud8Cd7YDSHexs1ODHo6fkTPrmnwI1g/edit?usp=sharing."
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

;; WARNING DF2003 lacks :all-entries-key values for a good subset of its rows ...

(def df-2003-sheet-name "DF2003--Master")
(def df-2003-worksheet-name "2003 final")
(def df-2003-max-col 125)
(def df-2003-max-row 278)

(def root-keys
  [:all-entries-key
   :df2003-page-ref
   :root-morpheme-break
   :morpheme-gloss
   :root-translation-1
   :root-translation-2
   :root-translation-3
   :transitivity
   :udb-class])

(def prs-keys
  [:prs-asp-tag
   :prs-asp-morpheme-break
   :prs-pp-tag
   :prs-pp-morpheme-break
   :prs-mid-refl-tag
   :prs-mid-refl-morpheme-break
   :prs-mod-tag
   :prs-mod-morpheme-break
   :prs-surface-form
   :prs-simple-phonetics
   :prs-syllabary
   :prs-translation])

(def impf-keys
  [:impf-asp-tag
   :impf-asp-morpheme-break
   :impf-pp-tag
   :impf-pp-morpheme-break
   :impf-mid-refl-tag
   :impf-mid-refl-morpheme-break
   :impf-mod-tag
   :impf-mod-morpheme-break
   :impf-surface-form
   :impf-simple-phonetics
   :impf-syllabary
   :impf-translation])

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
   :pft-simple-phonetics
   :pft-syllabary
   :pft-translation])

(def fut-impt-keys
  [:fut-impt-asp-tag
   :fut-impt-asp-morpheme-break
   :fut-impt-pp-tag
   :fut-impt-pp-morpheme-break
   :fut-impt-mid-refl-tag
   :fut-impt-mid-refl-morpheme-break
   :fut-impt-mod-tag
   :fut-impt-mod-morpheme-break
   :fut-impt-surface-form
   :fut-impt-simple-phonetics
   :fut-impt-syllabary
   :fut-impt-translation])

(def fut-keys
  [:fut-asp-tag
   :fut-asp-morpheme-break
   :fut-ppp-tag-1
   :fut-ppp-morpheme-break-1
   :fut-ppp-tag-2
   :fut-ppp-morpheme-break-2
   :fut-pp-tag
   :fut-pp-morpheme-break
   :fut-mid-refl-tag
   :fut-mid-refl-morpheme-break
   :fut-mod-tag
   :fut-mod-morpheme-break
   :fut-surface-form
   :fut-simple-phonetics
   :fut-syllabary
   :fut-translation])

(def pct-keys
  [:pct-asp-tag
   :pct-asp-morpheme-break
   :pct-ppp-tag
   :pct-ppp-morpheme-break
   :pct-pp-tag
   :pct-pp-morpheme-break
   :pct-mid-refl-tag
   :pct-mid-refl-morpheme-break
   :pct-mod-tag
   :pct-mod-morpheme-break
   :pct-surface-form
   :pct-simple-phonetics
   :pct-syllabary
   :pct-translation])

(def impt-keys
  [:impt-asp-tag
   :impt-asp-morpheme-break
   :impt-ppp-tag
   :impt-ppp-morpheme-break
   :impt-pp-tag
   :impt-pp-morpheme-break
   :impt-mid-refl-tag
   :impt-mid-refl-morpheme-break
   :impt-mod-tag
   :impt-mod-morpheme-break
   :impt-surface-form
   :impt-simple-phonetics
   :impt-syllabary
   :impt-translation])

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
   :inf-simple-phonetics
   :inf-syllabary
   :inf-translation])

(def df-2003-key-sets
  {:root root-keys
   :prs prs-keys
   :impf impf-keys
   :pft pft-keys
   :fut-impt fut-impt-keys
   :fut fut-keys
   :pct pct-keys
   :impt impt-keys
   :inf inf-keys})

(defn row-map->seq-of-form-maps
  [row-map]
  (map (fn [[verb-type keys]]
         (assoc (select-keys row-map keys) :verb-type verb-type))
       df-2003-key-sets))

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
   :df2003-page-ref "DF 2003 page reference"
   :all-entries-key "All entries key"
   :udb-class "UDB Class"})

(defmulti get-comments
  (fn [dailp-form-map _] (:verb-type dailp-form-map)))

(defmethod get-comments :root
  [dailp-form-map kwixer]
  (get-simple-field-value-comments dailp-form-map root-simple-comments-keys))

(defn get-feeling-comments
  "Return standard verb form comments: a Feeling 2003 reference and the
  transcription of the form in the numeric alphabet/orthography."
  [dailp-form-map kwixer]
  (format
   "Feeling 2003:%s."
   (get-in dailp-form-map [:root :df2003-page-ref])))

(defmethod get-comments :default
  [dailp-form-map kwixer]
  (get-feeling-comments dailp-form-map kwixer))

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
        ppp-kws
        (cond
          (some #{inflection} '(:fut)) [:ppp-morpheme-break-1 :ppp-tag-1
                                        :ppp-morpheme-break-2 :ppp-tag-2]
          (some #{inflection} '(:prs :impf :fut-impt)) []
          :else [:ppp-morpheme-break :ppp-tag])]
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
                                               [(kwixer :translation)])
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

(defmethod dailp-form-map->form-map :prs
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :prs))

(defmethod dailp-form-map->form-map :impf
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :impf))

(defmethod dailp-form-map->form-map :pft
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :pft))

(defmethod dailp-form-map->form-map :fut-impt
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :fut-impt))

(defmethod dailp-form-map->form-map :fut
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :fut))

(defmethod dailp-form-map->form-map :pct
  [state dailp-form-map]
  (dailp-surface-form-map->form-map state dailp-form-map :pct))

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
  df-2003-key-sets) of row-map has content."
  [row-map]
  (-> row-map
      :verb-type
      df-2003-key-sets
      (#(select-keys row-map %))
      vals
      ((partial filter identity))
      not-empty?))

;; WARNING DF2003 lacks :all-entries-key values for a good subset of its rows ...
(defn row-map-has-root-with-key? 
  [row-map]
  (get-in row-map [:root :all-entries-key]))

(defn row-map-lacks-root-with-key? 
  [row-map]
  (not (row-map-has-root-with-key? row-map)))

(defn remove-bad-dailp-form-maps
  [state dailp-form-maps]
  (->> dailp-form-maps
       (filter row-map-has-content?)))

(defn row-map->form-maps
  [state row-map]
  (->> row-map
       (row-map->dailp-form-maps state)
       (remove-bad-dailp-form-maps state)
       (dailp-form-maps->form-maps state)))

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
  (and (:root-morpheme-break row)
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

(defn fetch-upload-verbs-df-2003
  "Fetch verbs from Google Sheets, upload them to OLD, return state map with
  verbs-type-key submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (let [verbs-key :df-2003-verbs]
    (->> state
         (fetch-verbs-from-worksheet disable-cache
                                     df-2003-sheet-name
                                     df-2003-worksheet-name
                                     df-2003-max-col
                                     df-2003-max-row
                                     verbs-key)
         (apply-or-error (partial table->forms verbs-key))
         (apply-or-error (partial upload-verbs verbs-key))
         (apply-or-error (partial update-state-verbs state verbs-key))
         )))
