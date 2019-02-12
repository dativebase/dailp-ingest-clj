(ns dailp-ingest-clj.old-io
  (:require
   [old-client.core :refer [make-old-client]]))


(defn get-state
  ([] (get-state (make-old-client)))
  ([old-client]
   {:old-client old-client
    :created_pronominal_prefixes []
    :warnings {}
    :tags {}
    :syntactic-categories {}
    :sources-categories {}
    }))

