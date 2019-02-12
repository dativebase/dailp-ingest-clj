(ns dailp-ingest-clj.orthographies
  "Logic for ingesting DAILP orthographies."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.resources :refer [create-resource update-resource fetch-resources]]
            [old-client.models :refer [orthography]]
            [old-client.utils :refer [json-parse]]
            [dailp-ingest-clj.utils :refer [strip str->kw
                                            seq-rets->ret
                                            err->>
                                            apply-or-error]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [clojure.data.csv :as csv])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(def orthographies-file-path "resources/private/orthographies.csv")

(def orthographies-sheet-name "Orthographic Inventories")

(def orthographies-worksheet-name "Sheet1")

;; For ordering the graphs of each orthography.
(def orthography-segment-order
  ["short low (L)"
   "short high (H)"
   "short lowfall"
   "short superhigh"
   "long low (LL)"
   "long high (HH)"
   "rising (LH)"
   "falling (HL)"
   "lowfall (LF)"
   "superhigh (SH) = highfall"
   "short low (L)"
   "short high (H)"
   "short lowfall"
   "short superhigh"
   "long low (LL)"
   "long high (HH)"
   "rising (LH)"
   "falling (HL)"
   "lowfall (LF)"
   "superhigh (SH) = highfall"
   "short low (L)"
   "short high (H)"
   "short lowfall"
   "short superhigh"
   "long low (LL)"
   "long high (HH)"
   "rising (LH)"
   "falling (HL)"
   "lowfall (LF)"
   "superhigh (SH) = highfall"
   "short low (L)"
   "short high (H)"
   "short lowfall"
   "short superhigh"
   "long low (LL)"
   "long high (HH)"
   "rising (LH)"
   "falling (HL)"
   "lowfall (LF)"
   "superhigh (SH) = highfall"
   "short low (L)"
   "short high (H)"
   "short lowfall"
   "short superhigh"
   "long low (LL)"
   "long high (HH)"
   "rising (LH)"
   "falling (HL)"
   "lowfall (LF)"
   "superhigh (SH) = highfall"
   "short low (L)"
   "short high (H)"
   "short lowfall"
   "short superhigh"
   "long low (LL)"
   "long high (HH)"
   "rising (LH)"
   "falling (HL)"
   "lowfall (LF)"
   "superhigh (SH) = highfall"
   "any vowel"
   "unaspirated alveolar stop"
   "aspirated alveolar stop"
   "unaspirated velar stop"
   "aspirated velar stop"
   "unaspirated labiovelar stop"
   "aspirated labiovelar stop"
   "unaspirated alveolar affricate"
   "aspirated alveolar affricate"
   "lateral alveolar affricate"
   "voiceless alveolar affricate"
   "alveolar fricative"
   "glottal fricative"
   "glottal stop"
   "alveolar liquid"
   "palatal glide"
   "labiovelar glide"
   "bilabial nasal"])

(defn csv-data->maps
  "Convert a vector of vectors of strings (csv reader output) to a seq of maps;
  assumes first vector of strings is the header row of the CSV, which supplies
  keys for the resulting maps. This::

      (csv-data->maps [[a b] [1 2] [3 4]])

   becomes:

      ({a 1 b 2} {a 3 b 4})
  ."
  [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map (fn [x] (str->kw x)))
            repeat)
       (rest csv-data)))

(defn fix-segment
  "Return a new copy of orthography by removing the newlines, double quotes and
  spaces from the :segment value."
  [orthography]
  (assoc orthography
         :segment
         (-> (:segment orthography)
             (string/replace "\n" "")
             (strip " \""))))

(defn read-csv-io
  "Read the Orthographies CSV file, producing a lazy vector of strings, and
  return the result of running pure-processor on the input lazy vector."
  [csv-path pure-processor]
  (with-open [reader (io/reader csv-path)]
    (->> (csv/read-csv reader)
         pure-processor)))

(defn tmp
  [csv-path]
  (with-open [reader (io/reader csv-path)]
    (seq (csv/read-csv reader))))

(defn orthographies-csv->row-maps
  "Parse the orthographies CSV file and return a map for each row.
  The CSV structure::

      segment,       Orthography Name 1, Orthography Name 2
      short low (L), o,                  u

  returns::

      {:segment \"short low (L)\"
       :Orthography-Name-1 \"o\"
       :Orthography-Name-2 \"u\"}
  "
  [csv]
  (->> csv
       (map fix-segment)
       (filter #(seq (:segment %)))))

(defn get-base-orths-map
  "Return a map from orthography names to empty vectors."
  [orth-row-maps]
  (reduce (fn [agg [k v]] (if (= k :segment) agg (assoc agg k [])))
          {}
          (first orth-row-maps)))

(defn sort-orth-row-maps-by-segment
  "Sort the seq of orthography row maps according to their segment values, with
  the ordering defined by orthography-segment-order."
  [orth-row-maps]
  (sort #(compare
          (.indexOf orthography-segment-order (:segment %1))
          (.indexOf orthography-segment-order (:segment %2)))
        orth-row-maps))

(defn kw->human
  "Convert a keyword like :Dog-Cat to `Dog Cat`."
  [kw]
  (-> kw
      name
      (string/replace "-" " ")))

(defn assoc-row-map-to-orths-map
  "Return orths-map with the graphs in row-map appended to the values of the
  relevant keys. The orths-map is something like {:Orth-Name [\"a\" ...]} and
  the row-map is something like {:segment \"bilabial nasal\" :Orth-Name \"m\"}."
  [orths-map row-map]
  (reduce (fn [my-orths-map [orth-name graph]]
            (if (= orth-name :segment)
              my-orths-map
              (assoc my-orths-map orth-name
                     (conj (orth-name my-orths-map) graph))))
          orths-map
          row-map))

(defn get-orthographies-map
  "Return a map from orthography names (as keywords) to vectors of graphs in
  the orthography."
  [base-orths-map sorted-row-maps]
  (reduce assoc-row-map-to-orths-map
          base-orths-map
          sorted-row-maps))

(defn row-maps->rsrc-maps
  "Given a seq of row maps, return a seq of resource maps, where each resource
  can be used to create an OLD orthography."
  [row-maps]
  (let [base-orths-map (get-base-orths-map row-maps)
        sorted-row-maps (sort-orth-row-maps-by-segment row-maps)
        orths-map (get-orthographies-map base-orths-map sorted-row-maps)]
    (reduce (fn [agg [orth-name orth-vec]]
              (conj agg
                    (merge
                     orthography
                     {:name (kw->human orth-name)
                      :orthography (string/join ", " orth-vec)})))
            []
            orths-map)))

(defn csv->rsrc-maps
  "Convert the output of the CSV library (lazy vector of vector of stings) to
  a seq of resource maps that can be uploaded via HTTP request to an OLD."
  [csv]
  (-> csv
      csv-data->maps
      orthographies-csv->row-maps
      row-maps->rsrc-maps))

(defn extract-orthographies
  "Extract and process the orthographies in the CSV file at fp. The result is a
  seq of orthoraphy resource maps."
  [fp]
  [(read-csv-io fp csv->rsrc-maps) nil])

(defn update-orthography
  "Update the orthography matching orthography-map by name. Return a 2-element
  attempt vector."
  [state orthography-map]
  (let [existing-orthographies
        (fetch-resources (:old-client state) :orthography)
        orth-to-update
        (first (filter #(= (:name orthography-map) (:name %))
                       existing-orthographies))]
    (try+
     [(update-resource (:old-client state) :orthography (:id orth-to-update)
                       orthography-map)
      nil]
     (catch [:status 400] {:keys [body]}
       (if-let [error (-> body json-parse :error)]
         (if (= error (str "The update request failed because the submitted"
                           " data were not new."))
           [(format (str "Update not required for orthography '%s': it already"
                         " exists in its desired state.")
                    (:name orthography-map)) nil]
           [nil (format (str "Unexpected 'error' message when updating orthography"
                             " '%s': '%s'.")
                        (:name orthography-map)
                        (error))])
         [nil (format (str "Unexpected error updating orthography '%s'. No ':error'"
                           " key in JSON body."))]))
     (catch Object err
       [nil (format
             "Unknown error when attempting to update orthography named '%s': '%s'"
             (:name orthography-map)
             err)]))))

(defn create-orthography
  "Create an orthography from orthography-map. Update an existing orthography
  if one already exists with the specified name. In all cases, return a
  2-element attempt vector."
  [state orthography-map]
  (try+
   (create-resource (:old-client state) :orthography orthography-map)
   [(format "Created orthography '%s'." (:name orthography-map)) nil]
   (catch [:status 400] {:keys [request-time headers body]}
     (if-let [name-error (-> body json-parse :errors :name)]
       (update-orthography state orthography-map)
       [nil (json-parse body)]))
   (catch Object _
     [nil (format
           "Unknown error when attempting to create orthography named '%s'."
           (:name orthography-map))])))

(defn upload-orthographies 
  "Upload the seq of orthography resource maps to an OLD instance."
  [state orthographies]
  (seq-rets->ret (map (partial create-orthography state) orthographies)))

(defn extract-upload-orthographies
  "Extract the orthographies from CSV and upload them to an OLD instance.
  Should return a message string for each orthography upload attempt."
  [state]
  (apply-or-error
   (partial upload-orthographies state)
   (extract-orthographies orthographies-file-path)))

(defn fetch-orthographies-from-worksheet
  [disable-cache]
  [(-> (fetch-worksheet-caching {:spreadsheet orthographies-sheet-name
                                 :worksheet orthographies-worksheet-name}
                                disable-cache)
       csv-data->maps
       orthographies-csv->row-maps
       row-maps->rsrc-maps) nil])

(defn fetch-upload-orthographies
  "Fetch the orthographies from Google Sheets and upload them to an OLD
  instance. Should return a message string for each orthography upload attempt."
  ([state] (fetch-upload-orthographies state true))
  ([state disable-cache]
   (apply-or-error
    (partial upload-orthographies state)
    (fetch-orthographies-from-worksheet disable-cache))))
