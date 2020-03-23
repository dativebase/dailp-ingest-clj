(ns dailp-ingest-clj.verbs-df-1975
  "Logic for ingesting DAILP verbs from the Google Sheets DF1975--Master:
  https://docs.google.com/spreadsheets/d/11ssqdimOQc_hp3Zk8Y55m6DFfKR96OOpclUg5wcGSVE/edit?usp=sharing."
  (:require [clojure.pprint :as pprint]
            [clojure.spec.alpha :as s]
            [dailp-ingest-clj.google-io :as gio]
            [dailp-ingest-clj.old-io :as old-io]
            [dailp-ingest-clj.specs :as specs]
            [dailp-ingest-clj.tags :as tags]
            [dailp-ingest-clj.utils :as u]
            [dailp-ingest-clj.verbs :as verbs]
            [old-client.core :as oc]
            [old-client.models :as ocm]
            [old-client.resources :as ocr]))

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
  [_ row-map]
  (let [seq-of-form-maps (row-map->seq-of-form-maps row-map)]
    (map (fn [m] (assoc m :root (verbs/get-root-map seq-of-form-maps)))
         seq-of-form-maps)))

(defmulti dailp-form-map->form-map
  (fn [_ dailp-form-map] (:verb-type dailp-form-map)))

(def root-simple-comments-keys
  {:transitivity "Transitivity"
   :df1975-page-ref "DF 1975 page reference"
   :all-entries-key "All entries key"
   :udb-class "UDB Class"})

(defmulti get-comments
  (fn [dailp-form-map _] (:verb-type dailp-form-map)))

(defmethod get-comments :root
  [dailp-form-map _]
  (verbs/get-simple-field-value-comments dailp-form-map
                                         root-simple-comments-keys))

(defn get-feeling-numeric-comments
  "Return standard verb form comments: a Feeling 1975 reference and the
  transcription of the form in the numeric alphabet/orthography."
  [dailp-form-map kwixer]
  (format
   "Feeling 1975:%s (%s); narrow phonetic transcription source: Uchihara DB."
   (get-in dailp-form-map [:root :df1975-page-ref])
   (-> :numeric kwixer dailp-form-map)))

(defn get-numeric-comments
  [dailp-form-map kwixer]
  (if-let [numeric (-> :numeric kwixer dailp-form-map)]
    (format "Numeric phonetic transcription (source: Uchihara DB): %s."
            numeric)
    ""))

(defmethod get-comments :default
  [dailp-form-map kwixer]
  (get-numeric-comments dailp-form-map kwixer))

(defn page-ref->citation-tag-name
  [page-ref]
  (format "Source: DF1975: %s" page-ref))

(defn find-tag-id-by-page-ref
  [page-ref tags]
  (:id ((tags/get-tag-key {:name (page-ref->citation-tag-name page-ref)}) tags)))

(defn get-tags
  "Return a vector of tag IDs for the DF1975 verb form map first parameter.
  Always include the ingest tag ID; include the citation tag ID if one can be
  found."
  [{page-ref :df1975-page-ref}
   {{{ingest-tag-id :id} :ingest-tag :as tags} ::specs/tags-map :as state}]
  (when-not ingest-tag-id (println "ERROR: could not locate ingest tag!!!"))
  (if-let [citation-tag-id (find-tag-id-by-page-ref page-ref tags)]
    [ingest-tag-id citation-tag-id]
    [ingest-tag-id]))

(defn get-durbin-feeling-speaker
  [{:keys [speakers]}]
  (->> speakers
       vals
       (filter (fn [{:keys [first_name last_name]}]
                 (= [first_name last_name] ["Durbin" "Feeling"])))
       first
       :id))

(defn get-df-1975-source
  [{sources ::specs/sources-map}]
  (-> sources :feeling1975cherokee :id))

(defmethod dailp-form-map->form-map :root
  [state dailp-form-map]
  (let [translations (verbs/get-translations dailp-form-map
                                             verbs/root-translation-keys)
        form
        (ocm/create-form
         {::ocm/transcription (:root-morpheme-break dailp-form-map)
          ::ocm/morpheme_break (:root-morpheme-break dailp-form-map)
          ::ocm/morpheme_gloss (verbs/get-morpheme-gloss dailp-form-map translations)
          ::ocm/translations translations
          ::ocm/syntactic_category (get-in state [:syntactic-categories :V :id])
          ::ocm/source (get-df-1975-source state)
          ::ocm/comments (get-comments dailp-form-map nil)
          ::ocm/speaker (get-durbin-feeling-speaker state)
          ::ocm/tags (get-tags dailp-form-map state)})
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
  (let [kwixer (verbs/get-kwixer inflection)
        [mb mg] (verbs/compute-morpheme-break-gloss
                 dailp-form-map (get-standard-mb-mg-getter-vecs
                                 kwixer inflection))
        mb (or mb "")
        mg (or mg "")
        form-to-be-validated
        {::ocm/narrow_phonetic_transcription
         (or ((kwixer :surface-form) dailp-form-map) "")
         ::ocm/phonetic_transcription
         (or ((kwixer :simple-phonetics) dailp-form-map) "")
         ::ocm/transcription (or ((kwixer :syllabary) dailp-form-map) mb)
         ::ocm/morpheme_break mb
         ::ocm/morpheme_gloss mg
         ::ocm/translations (verbs/get-translations dailp-form-map
                                                    (verbs/get-translation-keys kwixer))
         ::ocm/syntactic_category (get-in state [:syntactic-categories :VP :id])
         ::ocm/source (get-df-1975-source state)
         ::ocm/comments (get-comments dailp-form-map kwixer)
         ::ocm/speaker (get-durbin-feeling-speaker state)
         ::ocm/tags (get-tags (:root dailp-form-map) state)}
        form (ocm/create-form form-to-be-validated)
        err (second form)]
    (if err
      [nil {:err err
            :all-entries-key (get-in dailp-form-map [:root :all-entries-key])
            :inflection inflection
            :dailp-form-map (verbs/succinct! dailp-form-map)
            :form-before-validation form-to-be-validated}]
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
      verbs/not-empty?))

(defn row-map-has-root-with-key?
  [row-map]
  (get-in row-map [:root :all-entries-key]))

(defn row-map-lacks-root-with-key?
  [row-map]
  (not (row-map-has-root-with-key? row-map)))

(defn remove-bad-dailp-form-maps
  [_ dailp-form-maps]
  (->> dailp-form-maps
       (filter row-map-has-root-with-key?)
       (filter row-map-has-content?)))

(defn row->forms
  [state row]
  (->> row
       (row-map->dailp-form-maps state)
       (remove-bad-dailp-form-maps state)
       (dailp-form-maps->form-maps state)))

(defn rows->forms
  "Return a sequence of Maybe vectors of forms."
  [{:keys [::specs/rows] :as state}]
  (printf "Getting forms from %s rows" (count rows))
  (assoc state
         ::specs/forms
         (reduce (fn [acc row] (concat acc (row->forms state row))) () rows)))

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

(defn calculate-rows
  "Given a `::specs/worksheet` vector of vectors, create a `::specs/rows` vector
  of row maps under `::specs/rows`."
  [{:keys [::specs/worksheet] :as state}]
  (u/just
   (assoc
    state
    ::specs/rows
    (->> worksheet
         verbs/table->row-maps
         project-roots))))

(defn extract-citation-tags
  "Set `::specs/citation-tags` to a sequence of tag maps, one for each unique
  `:df1975-page-ref` value from the spreadsheet rows."
  [{:keys [::specs/rows] :as state}]
  (u/just
   (assoc
    state
    ::specs/citation-tags
    (->> rows
         (map :df1975-page-ref)
         set
         (filter some?)
         (map (fn [page-ref]
                {:name (format "Source: DF1975: %s" page-ref)
                 :description
                 (format
                  (str "This form was taken from Cherokee-English"
                       " Dictionary, Durbin Feeling, 1975: %s.")
                  page-ref)}))))))

(defn upload-citation-tags
  "Upload the `::specs/citation-tags` to the target OLD and set that keyword to the
  updated tags (which should now have database IDs.)"
  [{citation-tags ::specs/citation-tags existing-tags ::specs/tags-map :as state}]
  (u/just-then
   (->> citation-tags
        (filter (fn [tag] (not ((tags/get-tag-key tag) existing-tags))))
        (tags/upload-tags state))
   (fn [uploaded-tags] (assoc state ::specs/citation-tags uploaded-tags))))

(defn extract-upload-citation-tags
  "Extract citation tags from the DF 1975 verb rows, and upload them to the
  target OLD. The result will be the addition of new tags to the
  `::specs/tags-map` map."
  [state]
  (u/just-then
   (u/err->> state
             extract-citation-tags
             upload-citation-tags)
   (fn [{citation-tags ::specs/citation-tags :as state}]
     (update state ::specs/tags-map merge
             (->> citation-tags
                  (map (fn [tag] [(tags/get-tag-key tag) tag]))
                  (into {}))))))

(defn fetch-worksheet!
  "Fetch the Google Sheets worksheet and store its data as a `::specs/worksheet`
  in `state`."
  [{:keys [::specs/disable-cache] :as state}]
  (u/just
   (assoc
    state
    ::specs/worksheet
    (gio/fetch-worksheet-caching {:spreadsheet-title df-1975-sheet-name
                                  :worksheet-title df-1975-worksheet-name
                                  :max-col df-1975-max-col
                                  :max-row df-1975-max-row}
                                 disable-cache))))

(defn triage-forms
  "Separate the errors from the true forms in `::specs/forms`. The errors are
  treated as warnings and stored in the state under `::specs/warnings`."
  [{forms ::specs/forms :as state}]
  (let [{:keys [forms warnings]}
        (group-by (fn [[_ err]] (if err :warnings :forms)) forms)]
    (-> state
        (assoc ::specs/forms (map first forms))
        (update ::specs/warnings concat (map second warnings)))))

(defn calculate-forms
  [state]
  (u/just
   (->> state
        rows->forms
        triage-forms)))

(defn upload!
  [{:keys [::specs/forms ::specs/upload-limit] :as state}]
  (u/just-then
   (u/maybes->maybe (map (partial verbs/create-verb state)
                         (if upload-limit (take upload-limit forms) forms)))
   (fn [forms] (update
                state
                ::specs/forms-map
                merge
                (verbs/verbs-seq->map forms)))))

(defn fetch-upload-verbs-df-1975
  "Fetch the DF1975--Master Google Sheet, produce a number of OLD froms by
  processing that sheet, and upload those forms to the target OLD. The
  `::specs/forms-map` of the state will be updated to contain the uploaded forms
  (keyed by their IDs)."
  [state & {:keys [disable-cache upload-limit] :or {disable-cache true}}]
  (u/err->> (merge state {::specs/disable-cache disable-cache
                          ::specs/upload-limit upload-limit})
            fetch-worksheet!
            calculate-rows
            extract-upload-citation-tags
            calculate-forms
            upload!))
