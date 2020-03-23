(ns dailp-ingest-clj.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [dailp-ingest-clj.aspectual-suffixes :as aspectual-suffixes]
            [dailp-ingest-clj.modal-suffixes :as modal-suffixes]
            [dailp-ingest-clj.old-io :as old-io]
            [dailp-ingest-clj.orthographies :as orthographies]
            [dailp-ingest-clj.prepronominal-prefixes :as prepronominal-prefixes]
            [dailp-ingest-clj.pronominal-prefixes :as pronominal-prefixes]
            [dailp-ingest-clj.sources :as sources]
            [dailp-ingest-clj.speakers :as speakers]
            [dailp-ingest-clj.specs :as specs]
            [dailp-ingest-clj.syntactic-categories :as syntactic-categories]
            [dailp-ingest-clj.tags :as tags]
            [dailp-ingest-clj.utils :as u]
            [dailp-ingest-clj.verbs-df-1975 :as verbs-df-1975]
            [dailp-ingest-clj.verbs-df-2003 :as verbs-df-2003]
            [old-client.core :as oc]
            [old-client.resources :as ocr]))

(defn store-state-on-disk
  [state]
  (spit "state.clj" (pr-str (select-keys state
                                         [::specs/tags-map
                                          ::specs/syntactic-categories-map
                                          ::specs/sources-map
                                          ::specs/speakers-map
                                          ::specs/forms-map
                                          ::specs/warnings
                                          ::specs/orthographies-map
                                          ])))
  (u/just state))

(defn summarize-ingest
  [state]
  {::specs/tags-map (count (::specs/tags-map state))
   ::specs/syntactic-categories-map (count (::specs/syntactic-categories-map state))
   ::specs/sources-map (count (::specs/sources-map state))
   ::specs/speakers-map (count (::specs/speakers-map state))
   ::specs/forms-map (count (::specs/forms-map state))
   ::specs/orthographies-map (count (:orthographies state))
   ::specs/warnings (::specs/warnings state)
   :keys (keys state)})

(defn merge-cached-state
  [state]
  (with-open [r (clojure.java.io/reader "state.clj")]
    [(merge state
           (edn/read (java.io.PushbackReader. r))) nil]))

(defn ingest
  "Perform an ingest of the DAILP Cherokee spreadsheet (Google) data into the OLD
  at `url`, with authentication via the provided credentials."
  [url username password]
  (u/err->> (old-io/get-state
             (oc/make-old-client {:url url
                                  :username username
                                  :password password}))
            #_merge-cached-state ;; TODO: keep commented out ...

            ;; START should be uncommented
            tags/fetch-upload-tags
            sources/fetch-upload-sources
            speakers/fetch-upload-speakers
            orthographies/fetch-upload-orthographies
            syntactic-categories/fetch-upload-syntactic-categories
            prepronominal-prefixes/fetch-upload-ppp-forms
            pronominal-prefixes/fetch-upload-pp-forms
            modal-suffixes/fetch-upload-mod-sfx-forms
            aspectual-suffixes/fetch-upload-asp-sfx-forms
            verbs-df-1975/fetch-upload-verbs-df-1975
            verbs-df-2003/fetch-upload-verbs-df-2003
            ;; (partial (fn [s] (verbs-df-1975/fetch-upload-verbs-df-1975
            ;;                   s :upload-limit 2)))
            ;; (partial (fn [s] (verbs-df-2003/fetch-upload-verbs-df-2003
            ;;                   s :upload-limit 2)))
            ;; END should be uncommented

            #_store-state-on-disk
            summarize-ingest))

(defn -main
  "Usage:

      $ lein run https://some.domain.com/path/to/old/instance/ someusername somepassword
  "
  [& args]
  (if (not args)
    (println
     (str "Please supply the URL, username and password of the OLD to which the"
          " DAILP data should be uploaded."))

    (do
      (println (apply (partial
                       format
                       (str "Attempting to upload the DAILP data to the OLD at '%s'"
                            " using username '%s' and password '%s'."))
                      args))
      (apply ingest args)
      (println "Success!"))))
