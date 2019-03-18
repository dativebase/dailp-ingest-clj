(ns dailp-ingest-clj.verbs
  (:require [clojure.string :as string]
            [clojure.pprint :as pprint]
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

(defn row->row-maps
  "Given a row (seq of strings and/or nils), return a map from keys in
  isomorphic header row to vals in row."
  [state header-row row]
  (->> row
       (map #(do [%1 %2]) header-row)
       (into {})))

(defn rows->row-maps
  [state header-row rows]
  (reduce
   (fn [agg row]
     (conj agg (row->row-maps state header-row row)))
   []
   rows))

(defn table->row-maps
  [state [_ header-row & rows]]
  (let [header-row (clean-verb-header-row header-row)]
    (->> rows
         ;; (take 10)
         remove-empty-rows
         (rows->row-maps state header-row)
    )))

(defn row-map->seq-of-form-maps
  [row-map]
  (map (fn [[verb-type keys]]
         (assoc (select-keys row-map keys) :verb-type verb-type))
       df-1975-key-sets))

(defn is-good-root-map?
  "Map m is a good root map if it is a :root type and if it has more than one
  non-nil value. Theverb type will always be there so that is why we need more
  than one."
  [m]
  (and
   (= (:verb-type m) :root)
   (> (->> m
           vals
           (filter identity)
           count)
      1)))

(defn get-root-map
  "Get the :root map from seq-of-form-maps if a good one can be found there. If
  not, get one from the map representing the next row."
  [seq-of-form-maps next-row-map next-next-row-map]
  (if-let [first-try (->> seq-of-form-maps
                          (filter is-good-root-map?)
                          first)]
    first-try
    (if-let [second-try
             (->> (row-map->seq-of-form-maps next-row-map)
                  (filter is-good-root-map?)
                  first)]
      second-try
      (->> (row-map->seq-of-form-maps next-next-row-map)
           (filter is-good-root-map?)
           first)
      )))

(defn row-map->dailp-form-maps
  "Given a row map (from a GSheet extraction), return a DAILP form map, i.e.,
  a map whose key/value pairs are from the GSheet and are largely unmodified."
  [state next-row-map next-next-row-map row-map]
  (let [seq-of-form-maps (row-map->seq-of-form-maps row-map)]
    (map (fn [m] (assoc m :root (get-root-map seq-of-form-maps next-row-map next-next-row-map)))
         seq-of-form-maps)))

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

(defn get-translation-keys
  "Return c(ou)nt translation keys corresponding to the keyword of kwixer."
  [kwixer & {:keys [cnt] :or {cnt 4}}]
  (map #(keyword (format "%s-%s" (name (kwixer %1)) %2))
       (repeat :translation)
       (range 1 cnt)))

(def prs-glot-grade-simple-comments-keys
  {})

(defmulti get-comments 
  (fn [dailp-form-map _] (:verb-type dailp-form-map)))

(defn get-simple-field-value-comments
  [dailp-form-map simple-comments-keys]
  (string/join
   " "
   (map (fn [[attr field-name]]
          (format "%s: %s." field-name (attr dailp-form-map)))
        simple-comments-keys)))

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
  (let [form
        (create-form
         {::ocm/transcription (:root-morpheme-break dailp-form-map)
          ::ocm/morpheme_break (:root-morpheme-break dailp-form-map)
          ::ocm/morpheme_gloss (:morpheme-gloss dailp-form-map)
          ::ocm/translations (get-translations dailp-form-map root-translation-keys)
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

(defn compute-morpheme-break-gloss
  "Compute and return as a 2-vec the morpheme break (mb) and morpheme gloss (mb)
  for a DAILP DF 1975 surface form."
  [dailp-form-map getter-vecs]
  (->> getter-vecs
       (map (fn [key-path] (get-in dailp-form-map key-path)))
       (partition 2)
       (filter (fn [toople] (not (some (zipmap [nil] (repeat true)) toople))))
       (apply (partial map (fn [& args] (string/join "-" args))))))

(defn get-kwixer
  "Given a prefix keyword, return a function that will prefix that keyword to
  any keyword passed into it. Keyword-prefixer = kwixer obvs."
  [prefix-kw]
  (fn [suffix-kw]
    (->> suffix-kw
         (list prefix-kw)
         (map name)
         (string/join "-")
         keyword)))

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

(defn succinct!
  [dailp-form-map]
  (->> dailp-form-map
       (filter (fn [[k v]] v))
       (into {})
       (#(dissoc % :root :verb-type))))

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

(defn monkeys
  [state dailp-form-map]
  (try
    (dailp-form-map->form-map state dailp-form-map)
    (catch Exception e
      [nil
       {:exception e
        :dailp-form-map dailp-form-map}])))

(defn dailp-form-maps->form-maps
  [state dailp-form-maps]
  ;; (map (partial dailp-form-map->form-map state) dailp-form-maps))
  (map (partial monkeys state) dailp-form-maps))

(def not-empty? (complement empty?))

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

(def transcription-type-suffixes
  ["surface-form"
   "numeric"
   "simple-phonetics"
   "syllabary"
   ""])

(defn is-transcription-type-key?
  [k]
  (let [n (name k)]
    (some true? (map #(string/ends-with? n %) transcription-type-suffixes))))

(defn get-transcription-type-keys
  [dailp-form-map]
  (->> dailp-form-map
       keys
       (filter is-transcription-type-key?)))

(defn row-map-has-transcription?
  "Return true if the row-map has some transcription-type value."
  [row-map]
  (-> row-map
      get-transcription-type-keys
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

(defn print-bad
  [bad next-row-map next-next-row-map & {:keys [border] :or {border false}}]
  ;; (println (:verb-type bad))
  ;; (pprint/pprint bad)
  ;; (println (keys bad))

  (if-let [good-vals
           (->> bad
                (filter (fn [[k v]] (and (not (= k :verb-type))
                                         v
                                         (not (map? v)))))
                seq)]
    (do
      (when border (println (string/join (take 80 (repeat \-)))))
      (pprint/pprint good-vals)
      (pprint/pprint (get-in bad [:root :all-entries-key]))
      (pprint/pprint (:all-entries-key next-row-map))
      (when border (println (string/join (take 80 (repeat \-)))))
    )
  ))

(defn remove-bad-dailp-form-maps
  [state next-row-map next-next-row-map dailp-form-maps]
  ;; FOX HERE: debug the :root-less maps
  (let [bad (first (filter row-map-lacks-root-with-key? dailp-form-maps))
        _ (when (seq bad)
            (print-bad bad next-row-map next-next-row-map :border true))]
    (->> dailp-form-maps
         (filter row-map-has-root-with-key?)
         (filter row-map-has-content?)
         ;; (filter row-map-has-transcription?)
         )))

(defn row-map->form-maps
  [state row-map next-row-map next-next-row-map]
  (->> row-map
       (row-map->dailp-form-maps state next-row-map next-next-row-map)
       (remove-bad-dailp-form-maps state next-row-map next-next-row-map)
       (dailp-form-maps->form-maps state)
  ))

(defn row-maps->form-maps
  [state row-maps]
  (->> (concat row-maps '({} {}))
       ;; (cons {})
       (partition 3 1)
       (reduce (fn [agg [row-map next-row-map next-next-row-map]]
                 (concat agg (row-map->form-maps state row-map next-row-map next-next-row-map)))
          ())))

(defn fix-key-less-row
  "Fix row by adding to it all of the root-targeting attr/vals that row lacks
  from last-full-row."
  [row last-full-row]
  (let [good-parts (->> (select-keys row root-keys)
                        (map (fn [[k v]] (when v [k v])))
                        (into {}))
        fixer (merge (select-keys last-full-row root-keys) good-parts)]
    (merge row fixer)))

(defn project-roots
  "Give good root attr-vals to all rows that lack them. The last seen good root
  attr-vals are assumed to apply to the current row, if it lacks good root
  attr-vals of its own."
  [row-maps]
  (reduce (fn [[last-full-row rows] row]
            (let [k (:all-entries-key row)
                  row (if k row (fix-key-less-row row last-full-row))
                  last-full-row (if k row last-full-row)]
              [last-full-row (conj rows row)]))
          [nil []]
          row-maps))

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

(defn fetch-verbs-from-worksheet
  "Fetch the verbs from the Google Sheets worksheet."
  [disable-cache sheet-name worksheet-name max-col max-row verbs-key state]
  [(->> (fetch-worksheet-caching {:spreadsheet-title sheet-name
                                  :worksheet-title worksheet-name
                                  :max-col max-col
                                  :max-row max-row}
                                 disable-cache)
        (assoc state verbs-key)) nil])

(defn upload-verbs 
  "Upload the seq of verb form resource maps to an OLD instance."
  [verbs-key state]
  (seq-rets->ret (map (partial create-verb state) (verbs-key state))))

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
         ;; (apply-or-error (partial upload-verbs verbs-key))
         ;; (apply-or-error (partial update-state-verbs verbs-key))
         )))
