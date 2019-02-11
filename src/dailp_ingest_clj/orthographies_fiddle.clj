(ns dailp-ingest-clj.orthographies-fiddle
  "For playing with/testing the orthographies.clj functionality."
  (:require [dailp-ingest-clj.orthographies :refer :all]
            [dailp-ingest-clj.old-io :refer :all]
            [dailp-ingest-clj.utils :refer [seq-rets->ret]]
            [old-client.core :refer [make-old-client]]
            [old-client.resources :refer :all]))

(defn delete-orthographies
  "Delete all orthographies in the OLD instance. Return a 2-element attempt
  vector."
  []
  (let [oc (make-old-client)]
    (seq-rets->ret
     (map (fn [o]
            (delete-resource oc :orthography (:id o))
            [(format "Deleted orthography '%s'." (:name o)) nil])
          (fetch-resources oc :orthography)))))

(defn fetch-orthographies
  []
  (fetch-resources (make-old-client) :orthography))

(comment

  (delete-orthographies)

  (fetch-orthographies)

  (extract-upload-orthographies (get-state))

  (first (extract-upload-orthographies (get-state)))

  (second (extract-upload-orthographies (get-state)))

)
