(ns dailp-ingest-clj.core-fiddle
  (:require [dailp-ingest-clj.core :refer [ingest]]))

(comment

  (ingest)

  (-> {:a [1 2]} :a)

  (-> {:a [1 2]} :a first)

  )

