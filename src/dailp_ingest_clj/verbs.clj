(ns dailp-ingest-clj.verbs
  "Common logic for ingesting DAILP verbs."
  (:require [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.models :as ocm :refer [form create-form]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            empty-str-or-nil->nil
                                            seq-rets->ret
                                            str->kw]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [create-resource-either]]))

(def fixme-translation "FIXME TRANSLATION NEEDED")
(def fixme-morpheme-gloss "FIXME.MORPHEME.GLOSS.NEEDED")

(defn create-verb
  "Create a verb from verb-map. Return a 2-element attempt vector where the
  first element (in the success case) is the verb map."
  [state verb-map]
  (create-resource-either state verb-map :resource-name :form))

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
         ;; (take 10)  ;; HERE TO REDUCE FOR DEV
         remove-empty-rows
         (rows->row-maps state header-row)
    )))

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
  "Get the :root map from seq-of-form-maps."
  [seq-of-form-maps]
  (->> seq-of-form-maps
       (filter is-good-root-map?)
       first))

(defn get-translations
  [dailp-form-map translation-keys]
  (let [actual-translations
        (->> translation-keys
             (map (fn [k]
                    (if-let [v (-> k dailp-form-map empty-str-or-nil->nil)]
                      {::ocm/transcription v ::ocm/grammaticality ""}
                      nil)))
             (filter identity)
             seq)]
    (if actual-translations
      actual-translations
      (list
       {::ocm/transcription fixme-translation
        ::ocm/grammaticality ""}))))

(defn translation->morpheme-gloss
  [translation]
  (string/replace translation #"\s+" "."))

(defn get-morpheme-gloss
  [dailp-form-map translations]
  (let [mg (:morpheme-gloss dailp-form-map)]
    (if mg mg
        (let [first-transl
              (-> translations first ::ocm/transcription)
              provisional-mg
              (if (= first-transl fixme-translation)
                fixme-morpheme-gloss
                (translation->morpheme-gloss first-transl))]
          (println provisional-mg)
          provisional-mg))))

(def root-translation-keys
  [:root-translation-1
   :root-translation-2
   :root-translation-3])

(defn get-translation-keys
  "Return c(ou)nt translation keys corresponding to the keyword of kwixer."
  [kwixer & {:keys [cnt] :or {cnt 4}}]
  (map #(keyword (format "%s-%s" (name (kwixer %1)) %2))
       (repeat :translation)
       (range 1 cnt)))

(defn get-simple-field-value-comments
  [dailp-form-map simple-comments-keys]
  (string/join
   " "
   (->> simple-comments-keys
        (map (fn [[attr field-name]]
               (when-let [val (attr dailp-form-map)]
                 (format "%s: %s." field-name val))))
        (filter identity))))

(defn compute-morpheme-break-gloss
  "Compute and return as a 2-vec the morpheme break (mb) and morpheme gloss (mb)
  for a DAILP DF 1975/2003 surface form."
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

(defn succinct!
  [dailp-form-map]
  (->> dailp-form-map
       (filter (fn [[k v]] v))
       (into {})
       (#(dissoc % :root :verb-type))))

(def not-empty? (complement empty?))

(def transcription-type-suffixes
  ["surface-form"
   "numeric"
   "simple-phonetics"
   "syllabary"
   ;; ""
   ])

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

(defn print-bad
  [bad & {:keys [border] :or {border false}}]
  (pprint/pprint bad)
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
      (pprint/pprint (:all-entries-key))
      (when border (println (string/join (take 80 (repeat \-))))))))

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

(defn update-state-verbs
  "Update state map's verbs-type-key map with the verbs in uploaded-verbs-ret."
  [state verbs-type-key uploaded-verbs-ret]
  (let [current-verbs (verbs-type-key state)]
    [(assoc state verbs-type-key
            (merge current-verbs (verbs-seq->map uploaded-verbs-ret))) nil]))
