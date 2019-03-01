(ns dailp-ingest-clj.verbs-fiddle
  (:require [clojure.string :as string]
            [dailp-ingest-clj.verbs :refer :all]
            [dailp-ingest-clj.fixtures :refer :all]))


(defn d->int [d] (-> d int (- 96)))

(defn tss! [d] (* 26 (d->int d)))

(defn one! [d] (d->int d))

(defn ab->int
  [[tss ones]]
  [tss ones]
  (let [[tss ones] (if ones [tss ones] [(char 96) tss])]
    [tss ones]
    [[tss ones]
     [(tss! tss) (one! ones)]
     (+ (tss! tss)
        (one! ones))
     ]
))

(def attrs-1
  [:a :b])



(def attrs-2
  [:a :c])

(def tmp-map {:a 1 :b 2 :c 3})

(def prs-glot-test
{:prs-ʔ-grade-asp-tag "PRS",
 :prs-ʔ-grade-simple-phonetics "gadaʔyiha",
 :prs-ʔ-grade-translation-2 nil,
 :prs-ʔ-grade-mod-morpheme-break "a",
 :prs-ʔ-grade-syllabary "ᎦᏓᏱᎭ",
 :prs-ʔ-grade-pp-tag "1SG.A",
 :prs-ʔ-grade-mod-tag "IND",
 :prs-ʔ-grade-numeric "gạ²dạʔ²yị³ha",
 :prs-ʔ-grade-mid-refl-tag nil,
 :prs-ʔ-grade-translation-1 "I'm denying it",
 :root
 {:root-translation-1 "deny",
  :transitivity "T",
  :root-morpheme-break "atay",
  :root-translation-2 nil,
  :morpheme-gloss "deny",
  :df1975-page-ref "3",
  :all-entries-key "25",
  :udb-class "5a.i",
  :verb-type :root,
  :root-translation-3 nil},
 :prs-ʔ-grade-mid-refl-morpheme-break nil,
 :prs-ʔ-grade-pp-morpheme-break "k",
 :prs-ʔ-grade-asp-morpheme-break "hi!h",
 :verb-type :prs-glot-grade,
 :prs-ʔ-grade-translation-3 nil,
 :prs-ʔ-grade-surface-form "gada'yi!ha",
 :prs-ʔ-grade-ppp-morpheme-break nil,
 :prs-ʔ-grade-ppp-tag nil})

(def x (list '("k" "1SG.A") '("atay" "deny") '("hi!h" "PRS") '("a" "IND")))

(comment

  (apply (partial map (fn [& args] (string/join "-" args))) x)

  (compute-mb-mg prs-glot-test)

  (apply juxt attrs-1)

  (some #{nil} '(nil 102))

  (some #{nil} '(101 102))

  (some (zipmap [1] (repeat true)) '(1 2 3))

  (some (zipmap [4] (repeat true)) '(1 2 3))

  (some (zipmap [nil] (repeat true)) '(1 2 3))

  (some (zipmap [nil] (repeat true)) '(nil 2 3))

  (zipmap [nil 1] (repeat true))

  (some {nil true} '(nil 2 3))

  (some {1 true} '(nil 2 3))

  ({1 true} 1)

  ({nil true} nil)


  ((apply juxt attrs-1) tmp-map)

  (select-keys tmp-map attrs-1)

  (select-keys tmp-map attrs-2)

  (select-keys {:a 1 :b 2 :c 3} [:a :z])

  (-> \a int (- 96))

  (-> \z int (- 96))

  (one! \a)

  (tss! \a)

  (ab->int "a")  ;; [nil \a]

  (ab->int "z")  ;; [nil \a]

  (ab->int "az")  ;; [nil \a]

  (ab->int "do") ;; [\d \o]

  (let [[a b] "ab"]
    [a b])

  (let [[a b] "a"]
    [a b])

  (+ 1 1)

  (hash-map 1 1)

  (map #(do [%1 %2]) [1 2] [3 4])

  (into {} [[1 2] [1 3] [3 4]])

  (fetch-upload-verbs fake-state)

  (fetch-upload-verbs fake-state false)

  (count (fetch-upload-verbs fake-state false))

  (filter identity (map :all-entries-key (fetch-upload-verbs fake-state false)))

  (get-in {:a {:b 2}} [:a :b])

  (:a {:a 2})

  (concat () {:a 2 :b 4})

  (dissoc {:a 2 nil 3} nil)

  (partition-by nil? [1 2 nil 3 4 5])
 
  (char 97)

  (conj () 1 2)

  (map char (range 97 (+ 97 26)))

  (seq "")

  (count (set [1 2 1]))


)




