(ns dailp-ingest-clj.core
  (:gen-class)
  (:require [dailp-ingest-clj.orthographies :refer [extract-upload-orthographies]]
            [old-client.core :refer [make-old-client]]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [state {:old-client (make-old-client)
               :warnings {}}]
    (extract-upload-orthographies state)))
