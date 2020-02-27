(ns dailp-ingest-clj.verbs-df-2003-fiddle
  (:require [dailp-ingest-clj.old-io :as old-io]
            [dailp-ingest-clj.fixtures :refer :all]
            [dailp-ingest-clj.sources :as sources]
            [dailp-ingest-clj.speakers :as speakers]
            [dailp-ingest-clj.specs :as specs]
            [dailp-ingest-clj.syntactic-categories :as syncats]
            [dailp-ingest-clj.tags :as tags]
            [dailp-ingest-clj.utils :as u]
            [dailp-ingest-clj.verbs-df-2003 :as sut]
            [old-client.core :as oc]
            [old-client.resources :as ocr]))

(def fiddle-state (atom {}))
(def url "http://127.0.0.1:61001/old")
(def username "admin")
(def password "adminA_1")

(def entities-meta
  {:tag [::specs/tags-map tags/tags-seq->map]
   :source [::specs/sources-map sources/sources-seq->map]
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


(comment

  (reset! fiddle-state {})

  (-> (get-test-state url username password)
      #_::specs/warnings
      #_::specs/tags-map
      ::specs/sources-map
      #_keys
      )

  (u/just-then
   (sut/fetch-upload-verbs-df-2003
    (get-test-state url username password)
    :disable-cache false
    :upload-limit 2)
   (fn [state]
     (-> state
         ::specs/forms-map
         #_::specs/warnings
         first
         )))

  (sut/fetch-upload-verbs-df-2003 fake-state)

  (sut/fetch-upload-verbs-df-2003 fake-state :disable-cache false)

  (let [x (sut/fetch-upload-verbs-df-2003 fake-state :disable-cache false)]
    (->> (-> x first :df-2003-verbs)
         (take 8)

        ))

)
