(ns dailp-ingest-clj.core-fiddle
  (:require [clojure.edn :as edn]
            [dailp-ingest-clj.core :refer [ingest]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.utils :refer [apply-or-error err->>]]
            [dailp-ingest-clj.resources :refer [delete-everything]]))

(comment

  (delete-everything (get-state))

  (ingest)

  (->> (ingest)
       first
       keys)

)
