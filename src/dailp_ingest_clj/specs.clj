(ns dailp-ingest-clj.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]))

(s/def ::key keyword?)
(s/def ::disable-cache boolean?)
(s/def ::raw-row vector?)
(s/def ::worksheet (s/coll-of ::raw-row))
(s/def ::row map?)
(s/def ::rows (s/coll-of ::row))

(s/def ::tag map?)
(s/def ::tags (s/coll-of ::tag))
(s/def ::tags-map
  (s/map-of keyword? ::tag))
(s/def ::citation-tags ::tags)

(s/def ::form map?)
(s/def ::forms (s/coll-of ::form))
(s/def ::forms-map
  (s/map-of int? ::form))

(s/def ::warning string?)
(s/def ::warnings (s/coll-of ::warning))

(comment

  (gen/generate (s/gen ::tags))

)
