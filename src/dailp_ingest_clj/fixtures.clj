(ns dailp-ingest-clj.fixtures
  (:require [old-client.core :refer [make-old-client]]))

;; Fake state map with all the tags, syntactic categories, and functional
;; affixal forms---so we don't have to make a bunch of request to test
;; ingest of higher-level things.
(def fake-state
  {:tags 1})
