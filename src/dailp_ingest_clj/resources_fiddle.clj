(ns dailp-ingest-clj.resources-fiddle
  (:require [dailp-ingest-clj.resources :refer :all]
            [old-client.core :refer [make-old-client]]
            [old-client.resources :refer [fetch-resources]]))

(def test-form
  {:transcription "xi",
   :morpheme_break "yi",
   :morpheme_gloss "IRR",
   :translations
   [{:transcription "Irrealis Prepronominal Prefix", :grammaticality ""}],
   :syntactic_category 26,
   :comments
   "H3 specification: +H3. tonicity: -tonic. TAOC: 27-29, 182. TAOC tag: IRR. Compare CRG (106-109) morpheme /yi- / glossed 'IRR' and named 'Irrealis Prepronominal Prefix'. Compare BMA 2008 (297-303) morpheme /yi-/ glossed 'IRR' and named 'Irrealis Prepronominal Prefix'.",
   :tags [124 148]})

;; Fake state: so we don't have to run the previous ingest steps just to get
;; the tags and syntactic categories in the state.
(defn get-fake-state
  []
  {:old-client (make-old-client)
   :created_pronominal_prefixes []
   :warnings {}
   :tags {:ingest-tag {:id 148}}
   :syntactic-categories {}
   :sources {}})

(comment

  (create-resource-with-unique-attr
   (get-fake-state)
   test-form
   :resource-name :form)

  (get-resource-query :form test-form
                      (get-in (get-fake-state) [:tags :ingest-tag :id]))

  (delete-all-resources (get-fake-state) :form)

  (second [1 2])

  (second [1])

  (second [])

  (upsert-resource
   (get-fake-state)
   test-form
   :resource-name :form)

  (count (upsert-resource
          (get-fake-state)
          test-form
          :resource-name :form))

  (seq? [])

  (coll? [])

  (coll? "a")

  (coll? 4)

  (flatten [{:a 2 :b 3} {:a 2 :b 4}])

  (fetch-resources (make-old-client) :form)

  (count (fetch-resources (make-old-client) :form))

)
