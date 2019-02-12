(ns dailp-ingest-clj.prepronominal-prefixes
  "Logic for ingesting DAILP prepronominal prefixes."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.resources :refer [create-resource update-resource fetch-resources]]
            [old-client.models :refer [orthography]]
            [old-client.utils :refer [json-parse]]
            [dailp-ingest-clj.utils :refer [strip str->kw
                                            seq-rets->ret
                                            err->>
                                            apply-or-error
                                            csv-data->maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [clojure.data.csv :as csv])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(def ppp-sheet-name "Orthographic Inventories")

(def ppp-worksheet-name "Sheet1")
