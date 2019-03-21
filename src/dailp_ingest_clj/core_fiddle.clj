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

  (dissoc {:a 2 :b 3} :a)

  (with-open [r (clojure.java.io/reader "state.clj")]
    (edn/read (java.io.PushbackReader. r)))

  (with-open [rdr (clojure.java.io/reader "state.clj")]
    (doall (map my-func (line-seq rdr))))

  (with-open [r (java.io.PushbackReader. (clojure.java.io/reader "state.clj"))]
    (binding [*read-eval* false]
      (read r)))

)
