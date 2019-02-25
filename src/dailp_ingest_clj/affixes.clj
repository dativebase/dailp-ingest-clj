(ns dailp-ingest-clj.affixes
  "Logic for transforming affix-related data structures."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.resources :refer [create-resource update-resource fetch-resources]]
            [old-client.models :refer [form]]
            [old-client.utils :refer [json-parse]]
            [dailp-ingest-clj.utils :refer [strip str->kw
                                            seq-rets->ret
                                            remove-nils
                                            err->>
                                            apply-or-error
                                            table->sec-of-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [clojure.data.csv :as csv])
  (:use [slingshot.slingshot :only [throw+ try+]]))

(defn gloss-is-neg
  [morpheme-gloss]
  (some #{morpheme-gloss} (list "NEG1" "NEG2")))

;; TODO: Ask Jeff why we are omitting these affixes.
(defn update-state-neg-gloss-warnings
  "Update state with warnings about negative PPP affixes being omitted."
  [morpheme-gloss affix-map state]
  (update-in state
             [:warnings :negative-PPP-omission-warnings]
             conj
             (format (str "Omitting ingestion of NEG morpheme with first"
                          " allomorph '%s'"
                          " glossed as '%s'.")
                     (:allomorph-1 affix-map)
                     morpheme-gloss)))

(defn get-comments-pp [affix-map] [])
(defn get-comments-refl [affiix-map] [])
(defn get-comments-mod [affiix-map] [])
(defn get-comments-cl [affiix-map] [])

(defn extract-text-references
  "Extract a seq of reference strings, each describing a reference to the
  morpheme described by affix-map in the specified text. Note that the values
  of the affix-map attributes targeted are assumed to be vectors of strings."
  [affix-map text-name text-attrs]
  (->> affix-map
       ((apply juxt text-attrs))
       (apply (partial map vector))
       (filter (fn [[x & _]] ((complement nil?) x)))
       (map (fn [[pp form tag morph-name]]
              (format
               "Compare %s (%s) morpheme /%s/ glossed '%s' and named '%s'."
               text-name pp form tag morph-name)))))

(defn extract-crg-references
  "Extract a seq of CRG strings, each describing a reference to affix-map in the
  CRG text. Note that the values of the affix-map attributes targeted (e.g.,
  :crg) are assumed to be vectors of strings."
  [affix-map]
  (extract-text-references affix-map "CRG"
                           (list :crg :crg-form :crg-tag :crg-morpheme-name)))

(defn extract-bma-2008-references
  "Extract a seq of BMA (2008) strings, each describing a reference to affix-map
  in the BMA (2008) text. Note that the values of the affix-map attributes
  targeted (e.g., :bma-2008) are assumed to be vectors of strings."
  [affix-map]
  (extract-text-references
   affix-map
   "BMA 2008"
   (list :bma-2008 :bma-2008-form :bma-2008-tag :bma-2008-morpheme-name)))

(defn extract-singleton-comments-fields
  "Return a seq of strings, each documenting a simple attribute-value pair from
  affix-map."
  [affix-map]
  (remove-nils
   (map (fn [[attr attr-name]]
          (when-let [val (attr affix-map)]
            (format "%s: %s." attr-name (string/trim val))))
        [[:h3-specification "H3 specification"]
         [:tonicity "tonicity"]
         [:taoc "TAOC"]
         [:taoc-tag "TAOC tag"]])))

(defn get-comments-ppp
  "Get a string of comments for a prepronominal prefix (PPP) from."
  [affix-map]
  (string/join
   " "
   (concat
    (extract-singleton-comments-fields affix-map)
    (extract-crg-references affix-map)
    (extract-bma-2008-references affix-map))))

(def comments-getters
  {:PP get-comments-pp
   :PPP get-comments-ppp
   :REFL get-comments-refl
   :MOD get-comments-mod
   :CL get-comments-cl})

;; Encodes mapping from prefix category to column names and the OLD tag
;; identifiers that correspond to them.
;;--------------+-------------+--------------+---------------+------+
;; allomorph 1  | allomorph 2 |  allomorph 3 |  allomorph 4  | CAT  |
;;--------------+-------------+--------------+---------------+------+
;; tee          | t           |  too         |               |      |
;; PPP_PRE_C    | PPP_PRE_VOC |  PPP_ELSE    |               | PPP  |
;;--------------+-------------+--------------+---------------+------+
;; ataat        | ataa        |  ata         |  at           | REFL |
;; REFL_PRE_VOC | REFL_PRE_C  |  REFL_PRE_C  |  REFL_PRE_A   |      |
;;--------------+-------------+--------------+---------------+------+
;; ali          | ataa        |  ata         |  at           | MID  |
;; REFL_PRE_H_S | REFL_PRE_C  |  REFL_PRE_C  |  REFL_PRE_VOC |      |
;;--------------+-------------+--------------+---------------+------+

;; The prefix-col->tag-idfr map associates category keywords to vectors of
;; 2-tuple vectors, where the first element is a table column keyword and
;; the second is the corresponding keyword tag identifier. The first element
;; of each 2-tuple vector should be a key in an affix map. The second element
;; should be the key of a tag under (:tags state). If the second element is a
;; map, its keys should be a category keyword.
(def prefix-col->tag-idfr
  {:PP [[:allomorph-1 :pp-pre-consonantal]
        [:allomorph-2 :pp-pre-vocalic]
        [:allomorph-3 :pp-pre-v]]
   :PPP [[:allomorph-1 :ppp-pre-consonantal]
         [:allomorph-2 :ppp-pre-vocalic]
         [:allomorph-3 :ppp-elsewhere]]
   :REFL [[:allomorph-1 {:REFL :refl-pre-vocalic
                         :MID :refl-pre-h-s}]
          [:allomorph-2 :refl-pre-consonantal]
          [:allomorph-3 :refl-pre-consonantal]
          [:allomorph-4 {:REFL :refl-pre-a
                         :MID :refl-pre-vocalic}]]
   :MOD [[:allomorph-1 :mod-pre-consonantal]
         [:allomorph-2 :mod-pre-vocalic]
         [:allomorph-3 :mod-pre-v]]
   :CL [[:allomorph-1 :cl-pre-consonantal]
        [:allomorph-2 :cl-pre-vocalic]
        [:allomorph-3 :cl-pre-v]]})

(defn -affix-map->seq-of-forms
  "Given an affix map (representing a row from a spreadsheet), return a sequence
  of form maps. One spreadsheet row may represent multiple forms because
  multiple allomorphs can be encoded in a single row."
  [affix-map state morpheme-gloss syncatkey]
  (let [translations [{:transcription (:morpheme-name affix-map)
                       :grammaticality ""}]
        syncat-id (get-in state [:syntactic-categories syncatkey :id])
        comments-getter (syncatkey comments-getters)
        comments (comments-getter affix-map)
        col-tag-pairs (syncatkey prefix-col->tag-idfr)]
    (filter
     identity
     (map
      (fn [[col-kw tag-kw]]
        (let [tag-kw (if (map? tag-kw) (syncatkey tag-kw) tag-kw)
              allomorph (string/trim (or (col-kw affix-map) ""))]
          (if (empty? allomorph)
            nil
            {:transcription allomorph,
             :morpheme_break allomorph,
             :morpheme_gloss morpheme-gloss,
             :translations translations,
             :syntactic_category syncat-id,
             :comments comments,
             :tags [(get-in state [:tags tag-kw :id])
                    (get-in state [:tags :ingest-tag :id])]})))
      col-tag-pairs))))

(defn affix-map->seq-of-forms
  "Produce a seq of zero or more forms from a map representing a single affix.
  Return a 2-vector containing the seq of forms and the state map, which may
  have been updated."
  [affix-map state & {:keys [syncatkey] :or {syncatkey :PPP}}]
  (let [morpheme-gloss (:tag affix-map)]
     (if (gloss-is-neg morpheme-gloss)  ;; NOTE: ignoring NEG-glossed affixes ...
       [(list) (update-state-neg-gloss-warnings morpheme-gloss affix-map state)]
       [(-affix-map->seq-of-forms affix-map state morpheme-gloss syncatkey) state])))
