(ns dailp-ingest-clj.manage
  "Management commands for dev/admin-level interaction with DAILP-related
  resources."
  (:require [cheshire.core :as ch]
            [clojure.pprint :as pprint]
            [clojure.walk :as w]
            [dailp-ingest-clj.utils :as u]
            [old-client.core :as oc]
            [old-client.resources :as ocr]))

(defn rsrc-kw->path
  [rsrc-kw]
  (if (= :corpus rsrc-kw)
    "corpora"
    (ocr/rsrc-kw->path rsrc-kw)))

(def page-size 10)

(defn count-resource
  "Issue the simplest possible GET /resources/ request that will allow us to get
  the count of that resource in the target OLD."
  [old-client resource]
  (try (-> old-client
           (oc/issue-request
            :get
            (rsrc-kw->path resource)
            {}
            {:page 1 :items_per_page 1})
           :paginator
           :count)
       (catch Exception e nil)))

(defn get-next-page
  [prev-page resource-count]
  (when (< (* prev-page page-size) resource-count) (inc prev-page)))

(defn generate-all-paginators
  "Return a lazy sequence of paginator maps required to fetch all resources with
  a count of `resource-count`."
  ([resource-count] (generate-all-paginators resource-count 0))
  ([resource-count prev-page]
   (lazy-seq
    (when-let [next-page (get-next-page prev-page resource-count)]
      (cons {:page next-page
             :items_per_page page-size}
            (generate-all-paginators resource-count next-page))))))

(def core-resources
  [:collection
   :corpus
   :file
   :form
   :formsearch
   :morphemelanguagemodel
   :morphology
   :morphologicalparser
   :orthography
   :page
   :phonology
   :source
   :speaker
   :syntacticcategory
   :tag
   :user])

(defn analyze
  "Analyze the data in a target OLD. Issue the necessary requests in order to
  determine the counts of all of the resources in `core-resources` and return a
  map of that information."
  [old-client]
  (->> core-resources
       (pmap (fn [resource] [resource (count-resource old-client resource)]))
       (into {})))

(defn get-next-paginator
  [{page :page items-per-page :items_per_page resource-count :count}]
  (let [next-page (inc page)
        next-resource (inc (* page items-per-page))]
    (when (<= next-resource resource-count)
      {:page next-page
       :items_per_page items-per-page})))

(defn fetch-all-of-resource
  ([old-client resource]
   (fetch-all-of-resource old-client
                          resource
                          {:page 1 :items_per_page page-size}
                          []))
  ([resource old-client paginator acc]
   (if-let [response
            (try (oc/issue-request old-client :get (rsrc-kw->path resource) {}
                                   paginator) (catch Exception e nil))]
     (if-let [next-paginator (get-next-paginator (:paginator response))]
       (fetch-all-of-resource old-client resource next-paginator
                              (concat acc (:items response)))
       (concat acc (:items response)))
     acc)))

(defn fetch-resources-with-paginator
  [old-client resource paginator]
  (try
    (oc/issue-request
     old-client
     :get
     (rsrc-kw->path resource)
     {}
     paginator)
    (catch Exception e [])))

(defn p-fetch-all-of-resource
  "Fetch all of resource matching keywrod `resource` in parallel."
  [old-client resource]
  (->> (count-resource old-client resource)
       generate-all-paginators
       (pmap (fn [paginator]
               (->> paginator
                    (fetch-resources-with-paginator old-client resource)
                    :items)))
       (apply concat)))

(defn backup
  "Fetch from the OLD targeted by `old-client` all of the resources of the
  resource types listed in the vector of keywords `resources` and store them locally on disk."
  [old-client resources path]
  (spit path
        (ch/generate-string
         (->> resources
              (pmap (fn [resource]
                      [resource (fetch-all-of-resource old-client resource)]))
              (into {}))
         {:pretty true})))

(defn fetch-all-ids-of-resource
  "Fetch all of the IDs of all of the resource matching keyword `resource`."
  [old-client resource]
  (map :id (p-fetch-all-of-resource old-client resource)))

(defn delete-all-of-resource
  [old-client resource]
  (->> (fetch-all-ids-of-resource old-client resource)
       (map
        (fn [id]
          (try
            (oc/issue-request old-client :delete
                              (format "%s/%s" (rsrc-kw->path resource) id))
            (catch Exception e nil))
          id))))

(defn delete-resources
  [old-client resources]
  )

(comment

  ;; Create a backup of the DAILP Dev Phonologies.
  (-> (u/get-dailp-dev-client)
      (backup [:phonology]
              "tmp/chrolddev-phonologies-2020-03-21.json"))

  ;; Fetch all of the IDs of the DAILP Dev Phonologies
  (-> (u/get-dailp-dev-client)
      (fetch-all-ids-of-resource :phonology)
      time
      with-out-str)

  (-> (u/get-dailp-dev-client)
      (fetch-all-ids-of-resource :form))

  (-> (u/get-dailp-dev-client)
      (delete-all-of-resource :phonology))

  (-> (u/get-dailp-dev-client)
      (delete-all-of-resource :form))

  (-> (u/get-dailp-dev-client)
      (delete-all-of-resource :tag))

  (generate-all-paginators 10)

  (-> (u/get-dailp-dev-client)
      (p-fetch-all-of-resource :form))

  (-> (p-fetch-all-of-resource (u/get-dailp-dev-client) :phonology)
      count)

  (-> (u/get-dailp-dev-client)
      analyze
      :form)

  (-> (u/get-dailp-dev-client)
      analyze)

  (-> (u/get-local-client)
      analyze)

  (-> (u/get-local-client)
      analyze
      time
      with-out-str)

)
