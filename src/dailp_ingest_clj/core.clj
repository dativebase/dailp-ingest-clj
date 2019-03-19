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
            [dailp-ingest-clj.pronominal-prefixes :refer [fetch-upload-pp-forms]]
            [dailp-ingest-clj.syntactic-categories :refer
             [fetch-upload-syntactic-categories]]
            [dailp-ingest-clj.tags :refer [fetch-upload-tags]]
            [dailp-ingest-clj.utils :refer [apply-or-error]]
            [dailp-ingest-clj.verbs :refer [fetch-upload-verbs]]))

(defn ingest
  []
  (->> [(get-state) nil]
       (apply-or-error fetch-upload-tags)
       (apply-or-error fetch-upload-orthographies)
       (apply-or-error fetch-upload-syntactic-categories)
       ;; (apply-or-error fetch-upload-ppp-forms)
       (apply-or-error fetch-upload-pp-forms)
       ;; (apply-or-error fetch-upload-mod-sfx-forms)
       ;; (apply-or-error fetch-upload-asp-sfx-forms)
       ;; (apply-or-error fetch-upload-verbs)
  )
)

(defn -main
  "TODO: add runtime configuration, e.g., OLD URL and auth credentials."
  [& args]
  (ingest))
