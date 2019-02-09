(ns dailp-ingest-clj.orthographies
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.core :refer [make-old-client]]
            [old-client.models :refer [orthography]]
            [dailp-ingest-clj.utils :refer [strip]]
            [clojure.data.csv :as csv]))

(def orthographies-file-path "resources/private/orthographies.csv")

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


(defn clean-for-kw
  "Clean an externally generated string so that it can be used as a keyword."
  [str-th]
  (-> str-th
      string/trim
      (string/replace #"," "")))

(defn spaces->hyphen
  [thing]
  (string/replace (clean-for-kw thing) #"\s+" "-"))

(defn csv-data->maps [csv-data]
  (mapv zipmap
       (->> (first csv-data) ;; First row is the header
            (map (fn [x] (keyword (spaces->hyphen x)))) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn fix-segment
  "Remove newlines, double quotes and spaces from the segment value."
  [orth]
  (assoc orth :segment (-> (:segment orth)
                           (string/replace "\n" "")
                           (strip " \""))))

(defn do-the-thing-IO
  []
  (with-open [reader (io/reader orthographies-file-path)]
    (->> (csv/read-csv reader)
         csv-data->maps)))

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
  []
  (with-open [reader (io/reader orthographies-file-path)]
    (->> (do-the-thing-IO)
         (zap fix-segment)
         (filter #(seq (:segment %))))))

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

(defn upload-orthographies 
  [state orthographies]
  (println "you wanna upload these orths"))

(defn extract-upload-orthographies
  [state]
  (upload-orthographies state (orthographies-csv->row-maps)))


(comment

  orthography

  (make-old-client)

  (row-maps->rsrc-maps (orthographies-csv->row-maps))

  (first (row-maps->rsrc-maps (orthographies-csv->row-maps)))

  (map :name (row-maps->rsrc-maps (orthographies-csv->row-maps)))

  (keys {:a 2})

  (conj [1] 2)

  (count (orthographies-csv->row-maps))

  (let [sorted (sort-orth-row-maps-by-segment (orthographies-csv->row-maps))]
    (-> sorted
        last))

  (let [sorted (sort-orth-row-maps-by-segment (orthographies-csv->row-maps))]
    (-> sorted
        last
        :segment))

  (let [sorted (sort-orth-row-maps-by-segment (orthographies-csv->row-maps))]
    (-> sorted
        first
        :segment))

  (sort #(compare (last %1) (last %2)) {:b 1 :c 3 :a  2})

)
