(ns dailp-ingest-clj.tags-fiddle
  (:require [clojure.string :as string]
            [old-client.core :refer [make-old-client]]
            [old-client.resources :refer :all]
            [dailp-ingest-clj.old-io :refer :all]
            [dailp-ingest-clj.utils :refer [seq-rets->ret]]
            [dailp-ingest-clj.tags :refer :all]))

(defn delete-tags
  "Delete all tags in the OLD instance. Return a 2-element
  attempt vector."
  []
  (let [oc (make-old-client)]
    (seq-rets->ret
     (map (fn [t]
            (if (#{"restricted" "foreign word"} (:name t))
              [(format "Unable to delete tag '%s'." (:name t)) nil]
              (do
                (delete-resource oc :tag (:id t))
                [(format "Deleted tag '%s'." (:name t)) nil])))
          (fetch-resources oc :tag)))))

(defn fetch-tags
  []
  (fetch-resources (make-old-client) :tag))

(comment

  (#{1 2} 11)

  (#{"restricted" "foreign word"} (:name {:name "restricted"}))

  (#{"restricted" "foreign word"} (:name {:name "restzicted"}))

  (get-now-iso8601)

  (get-ingest-tag-name)

  (get-ingest-tag-map)

  (delete-tags)  ;; in the OLD instance

  (fetch-tags)  ;; from the OLD instance

  (create-ingest-tag (get-state))  ;; in the OLD instance

  (fetch-all-tags false)

  (fetch-tags-from-worksheet true)  ;; from GSheets, cache disabled

  (fetch-tags-from-worksheet false)  ;; from GSheets, cache enabled

  (fetch-upload-tags (get-state))  ;; fetch from GSheets (cache disabled) and upload to OLD

  (fetch-upload-tags (get-state) false)  ;; fetch from GSheets (cache enabled) and upload to OLD

)
