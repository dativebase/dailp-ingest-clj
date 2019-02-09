
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

(ns dailp-ingest-clj.orthographies
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [dailp-ingest-clj.utils :refer [strip]]
            [clojure.data.csv :as csv]))

(def orthographies-file-path "resources/private/orthographies.csv")


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

(defn fix-orth
  [orth]
  (assoc orth :segment (-> (:segment orth)
                           (strip " \"\n")
                           )))

(defn orthographies-csv->maps
  []
  (with-open [reader (io/reader orthographies-file-path)]
    (csv-data->maps (csv/read-csv reader))))

(comment

  (orthographies-csv->maps)

  (sort #(compare (last %1) (last %2)) {:b 1 :c 3 :a  2})

)

(string/includes? "abc" "abd")

(defn upload-orthographies 
  [state orthographies]
  (println "you wanna upload these orths"))

(defn extract-upload-orthographies
  [state]
  (upload-orthographies state (extract-orthographies)))

