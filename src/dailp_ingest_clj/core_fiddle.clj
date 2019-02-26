(ns dailp-ingest-clj.core-fiddle
  (:require [dailp-ingest-clj.core :refer [ingest]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.resources :refer [delete-everything]]))

(comment

  (delete-everything (get-state))

  (ingest)

)

