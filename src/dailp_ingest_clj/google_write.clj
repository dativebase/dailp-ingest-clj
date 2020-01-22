(ns dailp-ingest-clj.google-write
  (:require [clojure.java.io :as io]
            [clojure.reflect :as r])
  (:import com.google.api.services.sheets.v4.Sheets
           #_com.google.api.services.sheets.v4.model
           com.google.common.collect.Lists))

(defn monkeys
  []
  Sheets)
