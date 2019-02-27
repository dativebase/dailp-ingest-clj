(ns dailp-ingest-clj.affixes-fiddle
  (:require [clojure.string :as string]
            [old-client.models :as ocm :refer [create-form]]
            [dailp-ingest-clj.affixes :refer :all]))

(def affix-map-vecs
  {:ref ["77-78" "101-103" nil]
   :ref-form ["etre" "etiez" "etes"]
   :ref-tag ["to.be" "you(pl) were" nil]
   :ref-morpheme-name ["To be" "To be" nil]})

(def affix-map-strs
  {:ref "77-78"
   :ref-form "etre"
   :ref-tag "to.be"
   :ref-morpheme-name "To be"})

(def text-attrs (list :ref :ref-form :ref-tag :ref-morpheme-name))

(comment

  (+ 9 3)

  (let [affix-map {:ref ["77-78" "101-103"]
                   :ref-form ["etre" "etiez"]
                   :ref-tag ["to.be" "you(pl) were"]
                   :ref-morpheme-name ["To be" "To be"]}]
    (extract-text-references
     affix-map "Test" (list :ref :ref-form :ref-tag :ref-morpheme-name)))

  (let [affix-map {:ref "77-78"
                   :ref-form "etre"
                   :ref-tag "to.be"
                   :ref-morpheme-name "To be"}]
    (extract-text-references
     affix-map "Test" (list :ref :ref-form :ref-tag :ref-morpheme-name)))

  (format-morpheme-break-affix-value "-óʔi / -o/-ooo")

  (format-morpheme-break-affix-value "-óʔi")

  (string/join "a" (list "b" "c"))

  (second (list 1))
  (if () "a" "b")

  ;; affix-map-vecs
  ;; affix-map-strs

  (->> affix-map-vecs
       ((apply juxt text-attrs))
       (map #(if (coll? %) % [%]))
       (apply (partial map vector))
       (filter (fn [[x & _]] ((complement nil?) x)))
       )  ;; [["77-78" "101-103"] ["etre" "etiez"] ["to.be" "you(pl) were"] ["To be" "To be"]]

  (->> affix-map-strs
       ((apply juxt text-attrs))
       (map #(if (coll? %) % [%]))
       (apply (partial map vector))
       )  ;; ["77-78" "etre" "to.be" "To be"]

  (coll? [])

  (coll? ())

  (coll? "abc")

  (vector "abc")

)


