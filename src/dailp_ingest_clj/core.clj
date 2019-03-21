(ns dailp-ingest-clj.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [old-client.core :refer [make-old-client]]
            [dailp-ingest-clj.aspectual-suffixes :refer
             [fetch-upload-asp-sfx-forms]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.modal-suffixes :refer [fetch-upload-mod-sfx-forms]]
            [dailp-ingest-clj.orthographies :refer [fetch-upload-orthographies]]
            [dailp-ingest-clj.prepronominal-prefixes :refer
             [fetch-upload-ppp-forms]]
            [dailp-ingest-clj.pronominal-prefixes :refer
             [fetch-upload-pp-forms fetch-upload-rm-pp-forms]]
            [dailp-ingest-clj.syntactic-categories :refer
             [fetch-upload-syntactic-categories]]
            [dailp-ingest-clj.tags :refer [fetch-upload-tags]]
            [dailp-ingest-clj.utils :refer [apply-or-error]]
            [dailp-ingest-clj.verbs-df-1975 :refer [fetch-upload-verbs-df-1975]]
            [dailp-ingest-clj.verbs-df-2003 :refer [fetch-upload-verbs-df-2003]]))

(defn store-state-on-disk
  [state]
  (spit "state.clj" (pr-str (select-keys state
                                         [:tags
                                          :sources
                                          :warnings
                                          :orthographies
                                          :syntactic-categories])))
  [state nil])

(defn summarize-ingest
  [state]
  (keys state)
  {:tags (count (:tags state))
   :syntactic-categories (count (:syntactic-categories state))
   :sources (count (:sources state))
   :mod-sfx-forms (count (:mod-sfx-forms state))
   :pp-forms (count (:pp-forms state))
   :asp-sfx-forms (count (:asp-sfx-forms state))
   :ppp-forms (count (:ppp-forms state))
   :orthographies (count (:orthographies state))
   :df-2003-verbs (count (:df-2003-verbs state))
   :warnings (:warnings state)
   :keys (keys state)})

(defn merge-cached-state
  [state]
  (with-open [r (clojure.java.io/reader "state.clj")]
    [(merge state
           (edn/read (java.io.PushbackReader. r))) nil]))

(defn ingest
  [url username password]
  (let [old-client
        (make-old-client {:url url
                          :username username
                          :password password})]
    (->> [(get-state old-client) nil]
         ;; (apply-or-error merge-cached-state)
         (apply-or-error fetch-upload-tags)
         (apply-or-error fetch-upload-orthographies)
         (apply-or-error fetch-upload-syntactic-categories)
         (apply-or-error fetch-upload-ppp-forms)
         (apply-or-error fetch-upload-pp-forms)
         (apply-or-error fetch-upload-mod-sfx-forms)
         (apply-or-error fetch-upload-asp-sfx-forms)
         (apply-or-error fetch-upload-verbs-df-1975)
         (apply-or-error fetch-upload-verbs-df-2003)
         (apply-or-error store-state-on-disk)
         (apply-or-error summarize-ingest))))

(defn -main
  "Usage:

      $ lein run https://some.domain.com/path/to/old/instance/ someusername somepassword
  "
  [& args]
  (if (not args)
    (println "Please supply the URL, username and password of the OLD to upload the DAILP data to.")
    (do
      (println (apply (partial
                       format
                       (str "Attempting to upload the DAILP data to the OLD at '%s'"
                            " using username '%s' and password '%s'."))
                      args))
      (apply ingest args)
      (println "Success!"))))
