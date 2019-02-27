(ns dailp-ingest-clj.core
  (:gen-class)
  (:require [old-client.core :refer [make-old-client]]
            [dailp-ingest-clj.aspectual-suffixes :refer
             [fetch-upload-asp-sfx-forms]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.modal-suffixes :refer [fetch-upload-mod-sfx-forms]]
            [dailp-ingest-clj.orthographies :refer [fetch-upload-orthographies]]
            [dailp-ingest-clj.prepronominal-prefixes :refer
             [fetch-upload-ppp-forms]]
            [dailp-ingest-clj.syntactic-categories :refer
             [fetch-upload-syntactic-categories]]
            [dailp-ingest-clj.tags :refer [fetch-upload-tags]]
            [dailp-ingest-clj.utils :refer [apply-or-error]]))

(defn ingest
  []
  (->> [(get-state) nil]
       (apply-or-error fetch-upload-orthographies)
       (apply-or-error fetch-upload-tags)
       (apply-or-error fetch-upload-syntactic-categories)
       (apply-or-error fetch-upload-ppp-forms)
       (apply-or-error fetch-upload-mod-sfx-forms)
       (apply-or-error fetch-upload-asp-sfx-forms)
  )
)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (ingest))
