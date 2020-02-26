(ns dailp-ingest-clj.verbs-df-1975-fiddle
  (:require [clojure.string :as str]
            [old-client.core :as oc]
            [old-client.models :as ocm]
            [old-client.resources :as ocr]
            [dailp-ingest-clj.old-io :as old-io]
            [dailp-ingest-clj.google-io :as gio]
            [dailp-ingest-clj.syntactic-categories :as syncats]
            [dailp-ingest-clj.sources :as sources]
            [dailp-ingest-clj.speakers :as speakers]
            [dailp-ingest-clj.specs :as specs]
            [dailp-ingest-clj.tags :as tags]
            [dailp-ingest-clj.utils :as u]
            [dailp-ingest-clj.verbs :as verbs]
            [dailp-ingest-clj.verbs-df-1975 :as df-1975]
            [clojure.pprint :as pprint]
            [dailp-ingest-clj.resources :as rs]
            [clojure.spec.alpha :as s]))

(def fiddle-state (atom {}))
(def url-old "http://127.0.0.1:61001/dailp")
(def url "http://127.0.0.1:61001/old")
(def username "admin")
(def password "adminA_1")

(def entities-meta
  {:tag [::specs/tags-map tags/tags-seq->map]
   :source [:sources sources/sources-seq->map]
   :speaker [:speakers speakers/speakers-seq->map]
   :syntactic-category [:syntactic-categories syncats/scs-seq->map]})

(defn set-cached-entities
  "Given keyword `entity`, set its pluralized keyword (e.g., `:entities`) to a
  map from IDs to resource maps. Performs a fetch if the values have not already
  been cached."
  [entity state]
  (let [[entities ->map] (entity entities-meta)]
    (if-let [cached (entities @fiddle-state)]
      (assoc state entities cached)
      (let [fetched (ocr/fetch-resources (:old-client state) entity)
            fetched-map (->map fetched)]
        (swap! fiddle-state (fn [s] (assoc s entities fetched-map)))
        (assoc state entities fetched-map)))))

(def set-cached-tags (partial set-cached-entities :tag))
(def set-cached-syncats (partial set-cached-entities :syntactic-category))
(def set-cached-sources (partial set-cached-entities :source))
(def set-cached-speakers (partial set-cached-entities :speaker))

(defn get-test-state
  [url username password]
  (let [old-client (oc/make-old-client {:url url
                                        :username username
                                        :password password})
        state (-> old-client
                  old-io/get-state
                  set-cached-tags
                  set-cached-syncats
                  set-cached-sources
                  set-cached-speakers)]
    state))

(defn fetch-process-df-1975-verbs
  [state]
  (let [verbs-key :df-1975-verbs
        state (update state :tmp merge
                      {:key verbs-key :disable-cache false})]
    (u/err->> state
              df-1975/fetch-worksheet!
              df-1975/calculate-rows
              df-1975/extract-upload-citation-tags
              df-1975/calculate-forms
              #_(partial df-1975/table->forms verbs-key))))

(def test-form-map
  {:impt-simple-phonetics nil,
   :impt-mid-refl-morpheme-break nil,
   :impt-mod-morpheme-break nil,
   :impt-asp-morpheme-break nil,
   :impt-ppp-tag-1 nil,
   :impt-ppp-morpheme-break-1 nil,
   :impt-pp-morpheme-break nil,
   :impt-asp-tag nil,
   :impt-translation-3 nil,
   :impt-translation-2 nil,
   :impt-translation-1 "Thunder!",
   :impt-mod-tag nil,
   :impt-numeric nil,
   :root
   {:root-translation-1 "thunder",
    :transitivity "I",
    :root-morpheme-break "ahyv:takwalo",
    :root-translation-2 nil,
    :morpheme-gloss nil,
    :df1975-page-ref nil,
    :all-entries-key "230",
    :udb-class "2b",
    :verb-type :root,
    :root-translation-3 nil},
    :impt-syllabary nil,
    :verb-type :impt,
    :impt-ppp-morpheme-break-2 nil,
    :impt-surface-form nil,
    :impt-ppp-tag-2 nil,
    :impt-pp-tag nil,
    :impt-mid-refl-tag nil})

(defn get-tag-names-string
  [form state]
  (str/join
   ", "
  (->> form
       :old-client.models/tags
       (map
        (fn [t-id]
          (->> state
               :tags
               vals
               (filter #(= t-id (:id %)))
               first
               :name))))))

(defn get-speaker-name-string
  [state form]
  (str/join " "
            (-> state
                :speakers
                (get (-> form :old-client.models/speaker))
                ((juxt :first_name :last_name)))))

(defn show-form-translations-tags-comments
  [state form]
  [(-> form :old-client.models/translations first :old-client.models/transcription)
   (get-tag-names-string form state)
   (:old-client.models/comments form)])

(defn inspect
  [state form]
  (conj (show-form-translations-tags-comments state form)
        (get-speaker-name-string state form)))

(comment

  (-> (get-test-state "http://127.0.0.1:61001/old" username password)
      ::specs/tags-map)

  (u/just-then
   (df-1975/fetch-upload-verbs-df-1975
    (get-test-state "http://127.0.0.1:61001/old" username password)
    :disable-cache false)
   (fn [state]
     (-> state
         ::specs/forms-map
         )))

  (speakers/fetch-upload-speakers (get-test-state url username password))

  (:tags (get-test-state url username password))

  (:speakers (get-test-state url username password))

  (->> (get-test-state url username password)
       :tags
       vals
       (filter #(= 21 (:id %)))
       first
       :name)

  (str/join " "
            (-> {:a "Joel" :b "Dunham"}
                ((juxt :a :b))))

  (let [state (get-test-state url username password)
        verbs-key :df-1975-verbs]
    (u/just-then
     (fetch-process-df-1975-verbs state)
     (fn [state]
       (->> state
            :tmp
            :df-1975-verbs
            (take 10)
            #_(map (partial show-form-translations-tags-comments state))
            (map (partial inspect state))
            ))))

  (:sources @fiddle-state)

  (sources/fetch-all-sources
   (get-test-state url username password))

  (sources/fetch-upload-sources
   (get-test-state url username password))

  @fiddle-state

  (reset! fiddle-state {})

  (ocr/fetch-resources
   (:old-client (get-test-state url username password))
   :zyntactic-category)

  (ocr/fetch-resources
   (:old-client (get-test-state url username password))
   :tag)

  (rs/delete-all-resources (get-test-state url username password)
                           :syntactic-categories)

  (syncats/fetch-syntactic-categories-from-worksheet true)

  (syncats/fetch-upload-syntactic-categories
   (get-test-state url username password))

  (->> (get-test-state url username password)
       :syntactic-categories
       vals
       (map :name))

  ;; Confirm that "Cherokee-English Dictionary" is the source for all of the
  ;; DF1975 verb forms.
  (let [state (get-test-state url username password)
        verbs-key :df-1975-verbs]
    (->> (fetch-process-df-1975-verbs state)
         verbs-key
         (take 100)
         (map (fn [{source-id :old-client.models/source}]
                (->> state
                     :sources
                     vals
                     (filter #(= source-id (:id %)))
                     first
                     :title)))))

  (let [state (get-test-state url username password)
        verbs-key :df-1975-verbs]
    (u/just-then
     (u/err->> state
               (partial verbs/fetch-worksheet!
                        false
                        df-1975/df-1975-sheet-name
                        df-1975/df-1975-worksheet-name
                        df-1975/df-1975-max-col
                        df-1975/df-1975-max-row
                        verbs-key)
               (partial df-1975/table->forms verbs-key))
     (fn [ret]
       {:warnings (:warnings ret)
        :df-1975-forms (-> ret verbs-key count)})))

  (let [state (get-test-state url username password)
        inflection :impt
        kwixer (verbs/get-kwixer inflection)
        getter-vecs (df-1975/get-standard-mb-mg-getter-vecs
                     kwixer inflection)
        dailp-form-map test-form-map]
    [getter-vecs
     (->> getter-vecs
          (map (fn [key-path] (get-in dailp-form-map key-path)))
          (partition 2)
          #_(filter (fn [toople] (not (some (zipmap [nil] (repeat true)) toople))))
          (filter (fn [x] (not (some nil? x))))
          ((fn [pairs]
            (if (seq pairs)
              (apply (partial map (fn [& args] (str/join "-" args))) pairs)
              [nil nil])))
          #_(apply (partial map (fn [& args] (str/join "-" args)))))]
    #_[kwixer getter-vecs]
    #_(df-1975/dailp-surface-form-map->form-map state dailp-form-map inflection))

  (verbs/get-translations test-form-map
                          (verbs/get-translation-keys (verbs/get-kwixer :impt)))

  (verbs/get-translation-keys (verbs/get-kwixer :impt))

)
