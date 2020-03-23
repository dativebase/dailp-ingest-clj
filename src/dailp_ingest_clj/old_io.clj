(ns dailp-ingest-clj.old-io
  (:require [dailp-ingest-clj.specs :as specs]
            [old-client.core :refer [make-old-client]]))

(defn get-state
  ([] (get-state (make-old-client)))
  ([old-client]
   {:old-client old-client
    ::specs/warnings {}
    ::specs/tags-map {}
    ::specs/forms-map {}
    ::specs/syntactic-categories-map {}
    ::specs/sources-map {}}))
