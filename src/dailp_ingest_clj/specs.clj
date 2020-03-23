(ns dailp-ingest-clj.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]))

(s/def ::key keyword?)
(s/def ::disable-cache boolean?)
(s/def ::raw-row vector?)
(s/def ::worksheet (s/coll-of ::raw-row))
(s/def ::row map?)
(s/def ::rows (s/coll-of ::row))

(s/def ::orthography map?)
(s/def ::orthographies (s/coll-of ::orthography))
(s/def ::orthographies-map (s/map-of int? ::orthography))

(s/def ::source map?)
(s/def ::sources (s/coll-of ::source))
(s/def ::sources-map (s/map-of keyword? ::source))

(s/def ::speaker map?)
(s/def ::speakers (s/coll-of ::speaker))
(s/def ::speakers-map (s/map-of int? ::speaker))

(s/def ::syntactic-category map?)
(s/def ::syntactic-categories (s/coll-of ::syntactic-category))
(s/def ::syntactic-categories-map (s/map-of keyword? ::syntactic-category))

(s/def ::tag map?)
(s/def ::tags (s/coll-of ::tag))
(s/def ::tags-map (s/map-of keyword? ::tag))
(s/def ::citation-tags ::tags)

(s/def ::upload-limit (s/nilable pos-int?))

(s/def ::form map?)
(s/def ::forms (s/coll-of ::form))
(s/def ::forms-map
  (s/map-of int? ::form))

(s/def ::warning string?)
(s/def ::warnings (s/coll-of ::warning))

(comment

  (gen/generate (s/gen ::tags))

)
