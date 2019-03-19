(ns dailp-ingest-clj.verbs-df-2003-fiddle
  (:require [dailp-ingest-clj.verbs-df-2003 :refer :all]
            [dailp-ingest-clj.fixtures :refer :all]))

(comment

  (+ 9 8)

  (fetch-upload-verbs-df-2003 fake-state)

  (fetch-upload-verbs-df-2003 fake-state :disable-cache false)

  (let [x (fetch-upload-verbs-df-2003 fake-state :disable-cache false)]
    (-> x
        first
        :df-2003-verbs
        count
        ;; first
        ))

)
