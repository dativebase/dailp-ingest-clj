(ns dailp-ingest-clj.prepronominal-prefixes-fiddle
  "For playing with/testing the prepronominal_prfixes.clj functionality"
  (:require [dailp-ingest-clj.prepronominal-prefixes :refer :all]
            [dailp-ingest-clj.old-io :refer :all]
            [dailp-ingest-clj.utils :refer [seq-rets->ret]]
            [old-client.core :refer [make-old-client]]
            [old-client.forms :refer :all]
            [old-client.resources :refer :all]))

(defn fetch-ppps
  "Fetch all forms with syntactic category 'PPP'."
  []
  (search-forms
   (make-old-client)
   {:query {:filter ["Form" "syntactic_category" "name" "=" "PPP"]}}))


(comment

  ;; (delete-ppps)  ;; in the OLD instance

  (fetch-ppps)  ;; from the OLD instance

  (fetch-syntactic-categories-from-worksheet false)

)
