(ns dailp-ingest-clj.syntactic-categories-fiddle
  "For playing with/testing the syntactic_categories.clj functionality"
  (:require [dailp-ingest-clj.syntactic-categories :refer :all]
            [dailp-ingest-clj.old-io :refer :all]
            [dailp-ingest-clj.utils :refer [seq-rets->ret]]
            [old-client.core :refer [make-old-client]]
            [old-client.forms :refer :all]
            [old-client.resources :refer :all]))

(defn delete-syntactic-categories
  "Delete all syntactic categories in the OLD instance. Return a 2-element
  attempt vector."
  []
  (let [oc (make-old-client)]
    (seq-rets->ret
     (map (fn [o]
            (delete-resource oc :syntactic-category (:id o))
            [(format "Deleted syntactic category '%s'." (:name o)) nil])
          (fetch-resources oc :syntactic-category)))))

(defn fetch-syntactic-categories
  []
  (fetch-resources (make-old-client) :syntactic-category))

(comment

  (delete-syntactic-categories)  ;; in the OLD instance

  (fetch-syntactic-categories)  ;; from the OLD instance

  (fetch-syntactic-categories-from-worksheet true)  ;; from GSheets, cache disabled

  (fetch-syntactic-categories-from-worksheet false)  ;; from GSheets, cache enabled

  (fetch-upload-syntactic-categories (get-state))  ;; fetch from GSheets (cache disabled) and upload to OLD

  (fetch-upload-syntactic-categories (get-state) false)  ;; fetch from GSheets (cache enabled) and upload to OLD

)
