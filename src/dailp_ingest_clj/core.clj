(ns dailp-ingest-clj.core
  (:gen-class)
  (:require [dailp-ingest-clj.orthographies :refer
             [extract-upload-orthographies]]
            [old-client.core :refer [make-old-client]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.tags :refer [fetch-upload-tags]]
            [dailp-ingest-clj.syntactic-categories :refer
             [fetch-upload-syntactic-categories]]))

(defn ingest
  []
  (let [state (get-state)]
    (-> state
        (fetch-upload-tags state)
        (fetch-upload-syntactic-categories))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [state {:old-client (make-old-client)
               :warnings {}}]
    (extract-upload-orthographies state)))
