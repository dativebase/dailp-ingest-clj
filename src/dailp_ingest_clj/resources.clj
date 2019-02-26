(ns dailp-ingest-clj.resources
  "Functions for interacting with OLD resources, i.e., creating, updating,
  fetching and/or deleting them."
  (:require [clojure.string :as string]
            [clj-time.core :as t]
            [old-client.resources :refer [create-resource
                                          update-resource
                                          fetch-resources
                                          delete-resource
                                          search-resources]]
            [old-client.utils :refer [json-parse]]
            [clj-time.format :as f]
            [dailp-ingest-clj.utils :refer [seq-rets->ret
                                            apply-or-error
                                            table->sec-of-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(defn get-now [] (t/now))

(defn get-now-iso8601
  "Return a legible ISO 8601 date-time string, e.g., '2019-02-22T21:05:45.721Z'."
  []
  (f/unparse (f/formatters :date-time) (get-now)))

(defn delete-all-resources
  "Delete all resources of type resource-name."
  [state resource-name]
  (let [client (:old-client state)]
    (map (fn [rsrc]
           (when (not (some #{[:tag "restricted"]
                              [:tag "foreign word"]}
                            (list [resource-name (:name rsrc)])))
             (delete-resource client resource-name (:id rsrc))))
         (fetch-resources (:old-client state) resource-name))))

(defn delete-everything
  "Delete all resources."
  [state]
  (map (fn [rsrc-name] (delete-all-resources state rsrc-name))
       [:form
        :tag
        :orthography
        :syntactic-category]))

;; Maps resource keywords to their OLD model string name.
(def rsrc-kws->names
  {:form "Form"})

(defn get-o2m-filter-exprs 
  "Return a vector of OLD filter expressions for one-to-many attribute attr-name.
  The attr-maps-seq arg is assumed to be a seq of maps."
  [rsrc-name attr-name attr-maps-seq]
  (reduce (fn [agg attr-map]
            (concat agg
                    (reduce (fn [agg2 [k v]]
                              (conj agg2 [rsrc-name attr-name (name k) "=" v]))
                            []
                            attr-map)))
          []
          attr-maps-seq))

(defn get-m2m-filter-exprs
  "Return a vector of OLD filter expressions for many-to-many attribute attr-name.
  The attr-val arg is assumed to be a seq of integer ids."
  [rsrc-name attr-name attr-val ingest-tag-id]
  (reduce (fn [agg id]
            (if (= [attr-name id] ["tags" ingest-tag-id])
              agg  ;; do NOT search based on the ingest tag id.
              (conj agg [rsrc-name attr-name "id" "=" id])))
          []
          attr-val))

;; Map resource keywords to their characteristic maps of many-to-one attributes.
(def m2o-rsrc-attrs
  {:form {:syntactic_category true}})

(defn get-filter-exprs
  "Return a vector of OLD filter expressions whose coordination should return
  the resource(s) of type rsrc whose attr has value attr-val. The ingest-tag-id
  is included so that we can exclude it from the search."
  [rsrc attr attr-val ingest-tag-id]
  (let [rsrc-name (rsrc rsrc-kws->names)
        attr-name (name attr)]
    (if (coll? attr-val)
      (if (int? (first attr-val))
        (get-m2m-filter-exprs rsrc-name attr-name attr-val ingest-tag-id)
        (get-o2m-filter-exprs rsrc-name attr-name attr-val))
      (if (get-in m2o-rsrc-attrs [rsrc attr])
        [[rsrc-name attr-name "id" "=" attr-val]]
        [[rsrc-name attr-name "=" attr-val]]))))

(defn get-resource-query
  "Return an OLD query data structure that finds a resource matching
  resource-map."
  [resource-name resource-map ingest-tag-id]
  (let [filter-expr
        (reduce (fn [agg [attr attr-val]]
                  (concat agg (get-filter-exprs
                               resource-name attr attr-val ingest-tag-id)))
                []
                resource-map)]
    {:query {:filter ["and" filter-expr]}}))

(defn upsert-resource
  "Upsert the resource. First search for an existing match. If it exists, update
  it with the current ingest tag. If it does not exist, create it."
  [state resource-map & {:keys [resource-name]}]
  (let [ingest-tag-id (get-in state [:tags :ingest-tag :id])
        query (get-resource-query resource-name resource-map ingest-tag-id)
        matches (search-resources (:old-client state) :form query)]
    (if (seq matches)
      (if (second matches)
        [nil (format
              (str "Failed in upsert-resource for resource of type %s; found"
                   " more than one existing match for this resource. Found %s"
                   " matches. This should not be possible.")
              resource-name (count matches))]
        (try+
         [(update-resource (:old-client state) resource-name
                           (-> matches first :id) resource-map) nil]
         (catch Object err
           [nil (format
                 "Unknown error when attempting to update %s resource with id '%s': '%s'"
                 resource-name (-> matches first :id) err)])))
      (try+
       [(create-resource (:old-client state) resource-name resource-map) nil]
       (catch Object err
         [nil (format
               (str
                "Unknown error when attempting to create a %s resource in"
                " upsert-resource: %s.")
               resource-name err)])))))

(defn update-resource-with-unique-attr
  "Update the existing resource that matches the supplied resource-map according
  to the value of unique-attr. Return a 2-element attempt vector where the first
  element (in the success case) is the resource map."
  [state resource-map & {:keys [resource-name unique-attr]}]
  (let [existing-resources
        (fetch-resources (:old-client state) resource-name)
        unique-val (unique-attr resource-map)
        resource-to-update
        (first (filter #(= unique-val (unique-attr %)) existing-resources))]
    (try+
     [(update-resource (:old-client state) resource-name
                       (:id resource-to-update) resource-map)
      nil]
     (catch [:status 400] {:keys [body]}
       (if-let [error (-> body json-parse :error)]
         (if (= error (str "The update request failed because the submitted"
                           " data were not new."))
           [resource-to-update nil]  ;; this is good: no need to update
           [nil (format (str "Unexpected 'error' message when updating %s resource"
                             " with %s value '%s': '%s'.")
                        resource-name unique-attr unique-val (error))])
         [nil (format (str "Unexpected error updating %s resource with %s value"
                           "'%s'. No ':error' key in JSON body.")
                      resource-name unique-attr unique-val)]))
     (catch Object err
       [nil (format
             "Unknown error when attempting to update %s resource with %s value '%s': '%s'"
             resource-name unique-attr unique-val err)]))))

(defn create-resource-with-unique-attr
  "Attempt to create a new resource using resource-map. If the create attempt
  fails because the resource already exists, i.e., because the value of
  unique-attr is not unique, then update the existing resource. In all cases,
  return a 2-element attempt vector where the first element (in the success
  case) is the resource map."
  [state resource-map & {:keys [resource-name unique-attr]}]
  (try+
   [(create-resource (:old-client state) resource-name resource-map) nil]
   (catch [:status 400] {:keys [body]}
     (if (-> body json-parse :errors unique-attr)
       (update-resource-with-unique-attr
        state
        resource-map
        :resource-name resource-name
        :unique-attr unique-attr)
       [nil (json-parse body)]))
   (catch Object err
     [nil (format
           (str
            "Unknown error when attempting to create a %s resource with an"
            " attribute %s whose value is '%s': '%s'.")
           resource-name unique-attr (unique-attr resource-map) err)])))

(defn upload-resources 
  "Upload the seq of resource resource maps to an OLD instance."
  [state resources]
  (seq-rets->ret (map (partial create-resource state) resources)))
