(ns dailp-ingest-clj.orthographies
  "Logic for ingesting DAILP orthographies."
  (:require [clojure.string :as string]
            [old-client.models :refer [orthography]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            kw->human
                                            strip
                                            seq-rets->ret
                                            table->sec-of-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.resources :refer [create-resource-with-unique-attr]]))

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

(defn fix-segment
  "Return a new copy of orthography by removing the newlines, double quotes and
  spaces from the :segment value."
  [orthography]
  (assoc orthography
         :segment
         (-> (:segment orthography)
             (string/replace "\n" "")
             (strip " \""))))

(defn fix-orth-rows
  [orth-rows]
  (->> orth-rows
       (filter #(seq (:segment %)))
       (map fix-segment)))

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

(defn create-orthography
  "Create an orthography from orthography-map. Update an existing orthography
  if one already exists with the specified name. In all cases, return a
  2-element attempt vector."
  [state orthography-map]
  (create-resource-with-unique-attr
   state
   orthography-map
   :resource-name :orthography
   :unique-attr :name))

(defn upload-orthographies 
  "Upload the seq of orthography resource maps to an OLD instance."
  [state orthographies]
  (seq-rets->ret (map (partial create-orthography state) orthographies)))

(defn fetch-orthographies-from-worksheet
  [disable-cache]
  [(-> (fetch-worksheet-caching {:spreadsheet-title orthographies-sheet-name
                                 :worksheet-title orthographies-worksheet-name
                                 :max-row 99
                                 :max-col 9}
                                disable-cache)
       table->sec-of-maps
       fix-orth-rows
       row-maps->rsrc-maps) nil])

(defn update-state-with-orthographies
  "Add the :orthogrpahies attribute to the state map; its value is the
  uploaded-orthographies-ret."
  [state uploaded-orthographies-seq]
  [(assoc state :orthographies
          (into {} (map (fn [o] [(:id o) o]) uploaded-orthographies-seq))) nil])

(defn fetch-upload-orthographies
  "Fetch the orthographies from Google Sheets and upload them to an OLD
  instance. Should return a message string for each orthography upload attempt."
  ([state] (fetch-upload-orthographies state true))
  ([state disable-cache]
   (->> (fetch-orthographies-from-worksheet disable-cache)
        (apply-or-error (partial upload-orthographies state))
        (apply-or-error (partial update-state-with-orthographies state)))))
