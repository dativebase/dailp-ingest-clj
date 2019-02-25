(ns dailp-ingest-clj.prepronominal-prefixes-fiddle
  "For playing with/testing the prepronominal_prfixes.clj functionality"
  (:require [dailp-ingest-clj.prepronominal-prefixes :refer :all]
            [dailp-ingest-clj.old-io :refer :all]
            [dailp-ingest-clj.utils :refer [seq-rets->ret table->sec-of-maps zipmapgroup]]
            [old-client.core :refer [make-old-client]]
            [old-client.forms :refer :all]
            [old-client.resources :refer :all]
            [clojure.string :as string]))

{
 :category "+H3",
 :allomorph-1 "yi",
 :allomorph-2 "y",
 :allomorph-3 "IRR",
 :tag "Irrealis Prepronominal Prefix",
 :morpheme-name "PPP",
 :tonicity "27-29, 182",
 :taoc "IRR",
 :crg-tag "Irrealis Prepronominal Prefix",
 :crg-morpheme-name "297-303",
 :taoc-tag "106-109",
 :h3-specification "-tonic",
 :crg "yi-",
 :crg-form "IRR",
 :syntacticcategory.name "Prepronominal Prefix"
 }

(defn fetch-ppps
  "Fetch all forms with syntactic category 'PPP'."
  []
  (search-forms
   (make-old-client)
   {:query {:filter ["Form" "syntactic_category" "name" "=" "PPP"]}}))


(defn delete-ppps
  "Delete all prepronominal prefix forms in the OLD instance. Return a 2-element
  attempt vector."
  []
  (let [oc (make-old-client)]
    (seq-rets->ret
     (map (fn [ppp]
            (delete-resource oc :form (:id ppp))
            [(format "Deleted syntactic category '%s'." (:name ppp)) nil])
          (fetch-ppps)))))

;; Fake state: so we don't have to run the previous ingest steps just to get
;; the tags and syntactic categories in the state.
(def fake-state
  {:old-client (make-old-client)
   :created_pronominal_prefixes []
   :warnings {}
   :tags
   {:ppp-pre-consonantal
    {:id 124
     :name "ppp-pre-consonantal"
     :description "Pre-consonantal prepronominal prefix"
     :datetime_modified "2019-02-12T18:51:42.989876"}
    :ingest-tag
    {:id 148
     :name "ingest-uchihara-root:2019-02-22T21:05:05.885Z"
     :description "The tag for the data ingest that occurred at 2019-02-22T21."
     :datetime_modified "2019-02-22T21:05:05.950675"}
    :mod-pre-v
    {:id 132
     :name "mod-pre-v"
     :description "Modal suffix allomorphs before the vowel /v/."
     :datetime_modified "2019-02-12T18:51:43.202184"}
    :refl-pre-h-s
    {:id 129
     :name "refl-pre-h-s"
     :description
     "Reflexive or middle prefix allomorphs before the segments /h/ or /s/."
     :datetime_modified "2019-02-12T18:51:43.119132"}
    :pp-pre-vocalic
    {:id 120
     :name "pp-pre-vocalic"
     :description "Pre-vocalic pronominal prefix"
     :datetime_modified "2019-02-12T18:51:42.904923"}
    :mod-pre-vocalic
    {:id 131
     :name "mod-pre-vocalic"
     :description "Modal suffix allomorphs before vowels."
     :datetime_modified "2019-02-12T18:51:43.174457"}
    :refl-pre-a
    {:id 128
     :name "refl-pre-a"
     :description "Reflexive or middle prefix allomorphs before the vowel /a/."
     :datetime_modified "2019-02-12T18:51:43.097230"}
    :cl-pre-consonantal
    {:id 133
     :name "cl-pre-consonantal"
     :description "Clitic allomorphs before consonants."
     :datetime_modified "2019-02-12T18:51:43.226965"}
    :pp-pre-consonantal
    {:id 121
     :name "pp-pre-consonantal"
     :description "Pre-consonantal pronominal prefix"
     :datetime_modified "2019-02-12T18:51:42.925118"}
    :cl-pre-vocalic
    {:id 134
     :name "cl-pre-vocalic"
     :description "Clitic allomorphs before vowels."
     :datetime_modified "2019-02-12T18:51:43.249347"}
    :pp-pre-v
    {:id 122
     :name "pp-pre-v"
     :description
     "Pre-v pronominal prefix. This tag marks the \"3SG.B\" allomorph \"uwa-\" of pre-consonantal \"uu-\" (and pre-vocalic \"uw-\"). It occurs before stem-initial /v-/, which is a schwa-like vowel in Cherokee."
     :datetime_modified "2019-02-12T18:51:42.946250"}
    :cl-pre-v
    {:id 135
     :name "cl-pre-v"
     :description "Clitic allomorphs before the vowel /v/."
     :datetime_modified "2019-02-12T18:51:43.275408"}
    :refl-pre-vocalic
    {:id 127
     :name "refl-pre-vocalic"
     :description "Reflexive or middle prefix allomorphs before vowels."
     :datetime_modified "2019-02-12T18:51:43.072933"}
    :mod-pre-consonantal
    {:id 130
     :name "mod-pre-consonantal"
     :description "Modal suffix allomorphs before consonants."
     :datetime_modified "2019-02-12T18:51:43.142739"}
    :refl-pre-consonantal
    {:id 126
     :name "refl-pre-consonantal"
     :description "Reflexive or middle prefix allomorphs before consonants."
     :datetime_modified "2019-02-12T18:51:43.043237"}
    :ppp-elsewhere
    {:id 125
     :name "ppp-elsewhere"
     :description
     "Elsewhere prepronominal prefix. Prepronominal prefix allomorphs marked with this tag (e.g., /too/) occur before the CISL1, CISL2, and ITER1 prepronominal prefixes."
     :datetime_modified "2019-02-12T18:51:43.011041"}
    :ppp-pre-vocalic
    {:id 123
     :name "ppp-pre-vocalic"
     :description "Pre-vocalic prepronominal prefix"
     :datetime_modified "2019-02-12T18:51:42.969600"}}

   :syntactic-categories
   {:V
    {:id 23
     :name "V"
     :type nil
     :description "Verbs"
     :datetime_modified "2019-02-12T19:24:14.014117"}
    :S
    {:id 24
     :name "S"
     :type "sentential"
     :description "Sentences"
     :datetime_modified "2019-02-12T19:24:14.034280"}
    :PP
    {:id 25
     :name "PP"
     :type nil
     :description "Pronominal Prefixes"
     :datetime_modified "2019-02-12T19:24:14.057406"}
    :PPP
    {:id 26
     :name "PPP"
     :type nil
     :description "Prepronominal Prefixes"
     :datetime_modified "2019-02-12T19:24:14.088661"}
    :REFL
    {:id 27
     :name "REFL"
     :type nil
     :description "Category for reflexive (REFL) and middle (MID) prefixes"
     :datetime_modified "2019-02-12T19:24:14.110072"}
    :MOD
    {:id 28
     :name "MOD"
     :type nil
     :description "Category for modal (MOD) suffixes"
     :datetime_modified "2019-02-12T19:24:14.130054"}
    :CL
    {:id 29
     :name "CL"
     :type nil
     :description "Category for clitics (CL)"
     :datetime_modified "2019-02-12T19:24:14.154862"}}

   :sources {}})

(def tmp
  {:allomorph-1 "yi"
   :allomorph-2 "y"
   :allomorph-3 nil
   :tag "IRR"
   :morpheme-name "Irrealis Prepronominal Prefix"
   :syntacticcategory.name "PPP"
   :category "Prepronominal Prefix"
   :h3-specification "+H3"
   :tonicity "-tonic"
   :taoc "27-29, 182"
   :taoc-tag "IRR"

   :crg ["106-109" nil nil]
   :crg-form ["yi- " nil nil]
   :crg-tag ["IRR" nil nil]
   :crg-morpheme-name ["Irrealis Prepronominal Prefix" nil nil]

   :bma-2008 ["297-303" nil]
   :bma-2008-form ["yi-" nil]
   :bma-2008-tag ["IRR" nil]
   :bma-2008-morpheme-name ["Irrealis Prepronominal Prefix" nil]})


(defn get-sheet-row-map
  []
  (-> (fetch-ppps-from-worksheet :disable-cache false)
      first
      first))

(def test-sheet-row-map
  {:allomorph-1 "yi"  ;; transcription and morpheme_break
   :allomorph-2 "y"  ;; transcription and morpheme_break
   :allomorph-3 nil  ;; transcription and morpheme_break
   :tag "IRR"  ;; morpheme_gloss
   :morpheme-name "Irrealis Prepronominal Prefix"  ;; translation.0.transcription

   :h3-specification "+H3"  ;; listed in comments field
   :tonicity "-tonic"  ;; listed in comments field
   :taoc "27-29, 182",  ;; listed in comments field
   :taoc-tag "IRR"  ;; listed in comments field

   :category "Prepronominal Prefix"  ;; not used
   :syntacticcategory.name "PPP"  ;; not used

   :crg ["106-109" nil nil]
   :crg-form ["yi-" nil nil]
   :crg-tag ["IRR" nil nil]
   :crg-morpheme-name ["Irrealis Prepronominal Prefix" nil nil]

   :bma-2008 ["297-303" nil]
   :bma-2008-form ["yi-" nil]
   :bma-2008-tag ["IRR" nil]
   :bma-2008-morpheme-name ["Irrealis Prepronominal Prefix" nil]
})

(comment

  ((juxt :crg :crg-tag) test-sheet-row-map)

  (let [crg-extractor (juxt :crg :crg-form :crg-tag :crg-morpheme-name)]
    (crg-extractor test-sheet-row-map))

  (let [crg-extractor (juxt :crg :crg-form :crg-tag :crg-morpheme-name)]
    (filter (fn [[frst & _]]
              ((complement nil?) frst))
            (apply (partial map vector) (crg-extractor test-sheet-row-map))))

  (->> test-sheet-row-map
        ((juxt :crg :crg-form :crg-tag :crg-morpheme-name))
        (apply (partial map vector))
        (filter (fn [[frst & _]] ((complement nil?) frst)))
        (map (fn [[pp form tag morph-name]]
               (format
                "Compare CRG (%s) morpheme /%s/ glossed '%s' and named '%s'."
                pp form tag morph-name)))
  )

  (map vector [1 2] [3 4])

  (test-sheet-row-map)

  (conj [1] 2)

  (delete-ppps)  ;; in the OLD instance

  (fetch-ppps)  ;; from the OLD instance

  (fetch-ppps-from-worksheet :disable-cache false)

  (fetch-ppps-from-worksheet :disable-cache true)

  (get-sheet-row-map)

  (affix-map->seq-of-forms test-sheet-row-map fake-state :syncatkey :PPP)

  (construct-ppp-form-maps :state fake-state)

  (fetch-upload-ppp-forms fake-state :disable-cache false)

  (list 1 2)

  (when-let [a nil] 2)

  (when-let [a true] 2)

  (filter (complement nil?) (list 1 false nil 3 nil))

  (flatten {:a 2 :b 3})

  (split-affix-map fake-ret fake-state)

  (table->sec-of-maps [["name" "city" "phone"]
                       ["joel" "surrey" "4242"]
                       ["tracy" "surrey" "8118"]])

  (table->sec-of-maps [["name" "city" "name"]
                   ["joel" "surrey" "dunham"]
                   ["tracy" "surrey" "merry"]])

  (map zipmap
       (repeat (list :a :b :c))
       [[1 2 3]
        [4 5 6]])

  (map zipmap
       (repeat (list :a :b :a))
       [[1 2 3]
        [4 5 6]])

  (zipmap [:a :b] [1 2])

  ((fn [key-vec val-vec]
     (reduce (fn [agg [k v]]
               [agg k v]
               ;; (update-in agg k v)
               (update agg k
                       (fn [old]
                         (if old
                           (conj [old] v)
                           v))))
             {}
             (map vector key-vec val-vec)))
   [:a :b :a]
   [1 2 3])

  (zipmapgroup [:a :b :a :a] [1 2 3 4])

  (table->sec-of-maps [["a" "b" "c" "a" "a"] [1 2 3 4 5] [\a \b \c \d \e]])

  (concat 1 1)

  (vector? [1])

  (seq nil)

  (conj [1] 2 3)

  (map (fn [key-vec val-vec]
         [key-vec val-vec])
       (repeat (list :a :b :a))
       [[1 2 3]
        [4 5 6]])



)
