(ns dailp-ingest-clj.pronominal-prefixes-fiddle
  (:require [dailp-ingest-clj.fixtures :refer :all]
            [dailp-ingest-clj.pronominal-prefixes :refer :all]))

(comment

  (+ 9 8)

  (update {} :a (partial merge {:a 2}))

  (update {:a {:b 4}} :a (partial merge {:a 2}))

  (update {} :a (fn [v] (concat v (list 1 2 3))))

  (update {:a '(66 77)} :a (fn [v] (concat v (list 1 2 3))))

  (fetch-upload-pp-forms fake-state :disable-cache false)

  (fetch-upload-ab-pp-forms fake-state :disable-cache false)

  (-> (fetch-upload-ab-pp-forms fake-state :disable-cache false)
      first
      :ab-pp-form-maps)

  (-> (fetch-upload-c-pp-forms fake-state :disable-cache false)
      first
      :c-pp-form-maps)

  (-> (fetch-upload-rm-pp-forms fake-state :disable-cache false)
      first
      :rm-pp-form-maps
  )


)
