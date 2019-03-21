(ns dailp-ingest-clj.tags-fiddle
  (:require [clojure.string :as string]
            [clj-time.format :as f]
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

  (keys (ns-publics 'clj-time.format))

  (f/show-formatters)

  (get-now-iso8601)

  (get-ingest-tag-name)

  (get-ingest-tag-map)

  (delete-tags)  ;; in the OLD instance

  (fetch-tags)  ;; from the OLD instance

  (fetch-all-tags false)

  (fetch-tags-from-worksheet true)  ;; from GSheets, cache disabled

  (fetch-tags-from-worksheet false)  ;; from GSheets, cache enabled

  (fetch-upload-tags (get-state))

  (fetch-upload-tags (get-state) :disable-cache false)

  (map (fn [x] (rand-int 10)) (range 10))

  (time (map (fn [x] (rand-int 10)) (range 10)))

  (map? "abc")

  (get {:a 2} :a "dog")

  (if (seq "") "y" "n")

  (empty? "ab")

  (if-let [a nil] a "frog")

  (if-let [a false] a "frog")

  (empty? "")

  (-> (fetch-upload-tags (get-state) :disable-cache false)
      :tags
      keys)

  (->> (fetch-upload-tags (get-state) :disable-cache false)
       :tags
       (map (fn [[key {:keys [id]}]] [key id])))

)
