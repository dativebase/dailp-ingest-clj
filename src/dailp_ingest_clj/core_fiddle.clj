(ns dailp-ingest-clj.core-fiddle
  (:require [clojure.edn :as edn]
            [dailp-ingest-clj.core :as core]
            [dailp-ingest-clj.old-io :as old-io]
            [dailp-ingest-clj.utils :refer [apply-or-error err->>]]
            [dailp-ingest-clj.resources :refer [delete-everything]]
            [old-client.core :as oc]))

(comment

  (let [url "http://127.0.0.1:61001/dailp"
        username "admin"
        password "adminA_1"]
    (core/ingest url username password))

  (oc/make-old-client {:url "http://127.0.0.1:61001/dailp"
                       :username "admin"
                       :password "adminA_1"})

  (old-io/get-state)

  (let [client (oc/make-old-client {:url "http://127.0.0.1:61001/dailp"
                                    :username "admin"
                                    :password "adminA_1"})
        state (old-io/get-state client)]
    #_state
    (oc/login client)
    )

  (old-io/get-state)

  (delete-everything (old-io/get-state))

  (core/ingest)

  (->> (core/ingest)
       first
       keys)

)
