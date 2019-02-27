(ns dailp-ingest-clj.affixes
  "Logic for transforming affix-related data structures."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pprint]
            [old-client.resources :refer [create-resource
                                          update-resource
                                          fetch-resources]]
            [old-client.models :as ocm :refer [form create-form]]
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
(defn get-comments-cl [affiix-map] [])

(defn quote-mb
  "Enclose mb in slash quotes."
  [mb] (format "/%s/" mb))

(defn format-morpheme-break-affix-value
  "Enclose mb in slash quotes. If there is more than one mb, but the second and
  subsequent into a parenthesized list. Returns a string. E.g., '-óʔi / -o'
  becomes '/-óʔi/ (/-o/)'."
  [mb]
  (let [[first & others] (->> (string/split mb #"/") (map string/trim))]
    (if (seq others)
      (format "%s (%s)" (quote-mb first)
              (string/join ", " (map quote-mb others)))
      (quote-mb first))))

(defn extract-text-references
  "Extract a seq of reference strings, each describing a reference to the
  morpheme described by affix-map in the specified text. Note that the values
  of the affix-map attributes targeted are assumed to be vectors of strings.
  Note further that  text-attrs is assumed to be a coll of exactly 4 items, a
  page reference (pp), a form, a tag (gloss) and a morpheme name (translation)."
  [affix-map text-name text-attrs]
  (->> affix-map
       ((apply juxt text-attrs)) ;; get seq of vectors of same type
       (map #(if (coll? %) % [%]))  ;; make into vectors if needed
       (apply (partial map vector))  ;; reorganize to seq of text ref tuples (vectors)
       (filter (fn [[x & _]] ((complement nil?) x)))  ;; remove any vec whose first el is nil
       (map (fn [[pp form tag morph-name]]
              (format
               "Compare %s (%s) morpheme %s glossed '%s' and named '%s'."
               text-name pp (format-morpheme-break-affix-value form) tag
               morph-name)))))

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
  [affix-map fields]
  (remove-nils
   (map (fn [[attr attr-name]]
          (when-let [val (attr affix-map)]
            (format "%s: %s." attr-name (string/trim val))))
        fields)))

(def ppp-fields
  [[:h3-specification "H3 specification"]
   [:tonicity "tonicity"]
   [:taoc "TAOC"]
   [:taoc-tag "TAOC tag"]])

(defn get-comments-ppp
  "Get a string of comments for a prepronominal prefix (PPP) from."
  [affix-map]
  (string/join
   " "
   (concat
    (extract-singleton-comments-fields affix-map ppp-fields)
    (extract-crg-references affix-map)
    (extract-bma-2008-references affix-map))))

(def mod-fields
  [[:taoc "TAOC"]])

(defn get-comments-mod
  [affix-map]
  (string/join
   " "
   (concat
    (extract-singleton-comments-fields affix-map mod-fields)
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
         [:allomorph-3 :mod-pre-v]
         [:allomorph-4 :mod-pre-v]]  ;; TODO: ask Jeff what tag should be used for this modal suffix
   :CL [[:allomorph-1 :cl-pre-consonantal]
        [:allomorph-2 :cl-pre-vocalic]
        [:allomorph-3 :cl-pre-v]]})

(defn -affix-map->seq-of-forms
  "Given an affix map (representing a row from a spreadsheet), return a sequence
  of form maps. One spreadsheet row may represent multiple forms because
  multiple allomorphs can be encoded in a single row."
  [affix-map state morpheme-gloss syncatkey]
  (let [translations [{::ocm/transcription (:morpheme-name affix-map)
                       ::ocm/grammaticality ""}]
        syncat-id (get-in state [:syntactic-categories syncatkey :id])
        comments-getter (syncatkey comments-getters)
        comments (comments-getter affix-map)
        col-tag-pairs (syncatkey prefix-col->tag-idfr)]
     (->> (map
           (fn [[col-kw tag-kw]]
             (let [tag-kw (if (map? tag-kw) (syncatkey tag-kw) tag-kw)
                   allomorph (string/trim (or (col-kw affix-map) ""))]
               (if (empty? allomorph)
                 nil
                 (create-form {::ocm/transcription allomorph,
                               ::ocm/morpheme_break allomorph,
                               ::ocm/morpheme_gloss morpheme-gloss,
                               ::ocm/translations translations,
                               ::ocm/syntactic_category syncat-id,
                               ::ocm/comments comments,
                               ::ocm/tags
                               [(get-in state [:tags tag-kw :id])
                                (get-in state [:tags :ingest-tag :id])]}))))
           col-tag-pairs)
          (filter identity))))

(defn affix-map->seq-of-forms
  "Produce a seq of zero or more forms from a map representing a single affix.
  Return a 2-vector containing the seq of forms and the state map, which may
  have been updated."
  [affix-map state & {:keys [syncatkey] :or {syncatkey :PPP}}]
  (let [morpheme-gloss (:tag affix-map)]
     (if (gloss-is-neg morpheme-gloss)  ;; NOTE: ignoring NEG-glossed affixes ...
       [() (update-state-neg-gloss-warnings morpheme-gloss affix-map state)]
       [(-affix-map->seq-of-forms affix-map state morpheme-gloss syncatkey) state])))

(defn extract-affix-forms-to-agg
  "Extract a seq of zero or more affix form maps from affix-map and store them
  in agg under the affix-key key. The syncatkey is a keyword that identifies
  the type of affix being extracted."
  [affix-key syncatkey agg affix-map]
  (let [[seq-of-forms new-state]
        (affix-map->seq-of-forms
         affix-map (:state agg) :syncatkey syncatkey)]
    (-> (assoc agg :state new-state)
        (update affix-key
                (fn [old-form-maps]
                  (concat old-form-maps seq-of-forms))))))

(defn construct-affix-form-maps
  "Return an either whose value is a map whose keys are :state and
  affix-form-maps-kw. The state value may have warnings added to it. The
  value of affix-form-maps-kw should be a seq of form maps representing
  affixes of the target type."
  [state affixes-seq affix-form-maps-kw affix-syncatkey]
  (let [ret (-> (reduce (partial extract-affix-forms-to-agg
                                 affix-form-maps-kw affix-syncatkey)
                        {:state state  affix-form-maps-kw ()}
                        affixes-seq)
                (update affix-form-maps-kw seq-rets->ret))]
    (apply-or-error
     (fn [_] [(update ret affix-form-maps-kw first) nil])
     (affix-form-maps-kw ret))))
