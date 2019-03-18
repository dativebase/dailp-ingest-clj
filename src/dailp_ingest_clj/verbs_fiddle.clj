(ns dailp-ingest-clj.verbs-fiddle
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]
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

(def nil-form-map
  {:impf-mod-tag nil,
   :impf-syllabary nil,
   :impf-numeric nil,
   :impf-ppp-morpheme-break nil,
   :impf-translation-1 nil,
   :impf-asp-morpheme-break nil,
   :impf-ppp-tag nil,
   :impf-translation-3 nil,
   :impf-surface-form nil,
   :impf-pp-tag nil,
   :impf-asp-tag nil,
   :impf-mid-refl-tag nil,
   :impf-mod-morpheme-break nil,
   :root
   {:root-translation-1 nil,
    :transitivity nil,
    :root-morpheme-break nil,
    :root-translation-2 nil,
    :morpheme-gloss nil,
    :df1975-page-ref nil,
    :all-entries-key nil,
    :udb-class nil,
    :verb-type :root,
    :root-translation-3 nil},
   :impf-mid-refl-morpheme-break nil,
   :impf-translation-2 nil,
   :impf-simple-phonetics nil,
   :verb-type :impf,
   :impf-pp-morpheme-break nil})

(def non-nil-form-map
  {:impf-mod-tag nil,
   :impf-syllabary nil,
   :impf-numeric nil,
   :impf-ppp-morpheme-break nil,
   :impf-translation-1 nil,
   :impf-asp-morpheme-break nil,
   :impf-ppp-tag nil,
   :impf-translation-3 nil,
   :impf-surface-form nil,
   :impf-pp-tag nil,
   :impf-asp-tag nil,
   :impf-mid-refl-tag nil,
   :impf-mod-morpheme-break nil,
   :root
   {:root-translation-1 nil,
    :transitivity nil,
    :root-morpheme-break nil,
    :root-translation-2 nil,
    :morpheme-gloss nil,
    :df1975-page-ref nil,
    :all-entries-key nil,
    :udb-class nil,
    :verb-type :root,
    :root-translation-3 nil},
   :impf-mid-refl-morpheme-break nil,
   :impf-translation-2 nil,
   :impf-simple-phonetics nil,
   :verb-type :impf,
   :impf-pp-morpheme-break ""})

(defn three-args
  [a b c]
  (list a b c))

(comment

  (three-args 1 2 3)

  ((partial three-args 1 2) 3)

  (row-map-has-content? nil-form-map)

  (row-map-has-content? non-nil-form-map)

  (row-map-has-transcription? non-nil-form-map)

  (-> non-nil-form-map
      get-transcription-type-keys
      (#(select-keys non-nil-form-map %))
      ;; vals
      ;; ((partial filter identity))
      ;; not-empty?
      )

  (get-transcription-type-keys nil-form-map)

  (remove-bad-dailp-form-maps
   {} '(nil-form-map
        non-nil-form-map))

  (->> nil-form-map
       keys
       (map name)
       (filter ends-with-transcription-type-suffix?)
  )

  (some true? '(true))

  (some true? '(false true false))

  (string/ends-with? "abc" "c")



  (select-keys
   nil-form-map
   ((:verb-type nil-form-map) df-1975-key-sets))

  (not-empty? ())

  (not-empty? '(1))

  (row-map-has-content? {} nil-form-map)

  (row-map-has-content? {} non-nil-form-map)

  (-> nil-form-map
      :verb-type
      df-1975-key-sets
      (#(select-keys nil-form-map %)) 
      vals
      ((partial filter identity))
      seq
  )

  (-> {:a 2}
      :a
      (#(do %))
      )

  (->> nil-form-map
       (select-keys (-> nil-form-map :verb-type df-1975-key-sets))

       )



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

  (partition 2 1 (cons nil [1 2 3 4 5 6 7]))

  (partition 3 1 '(:monkeys :monkets :monkeys) '(1 2 3))

  (list (range 10))

  (let [x (range 10)
        ]
    (->> x
         (cons {})
         (partition 2 1)
         (reduce (fn [agg [prev curr]] (concat agg (list {:prev prev :curr curr})))
                 ())
         ))

  (let [x (range 10)
        ]
    (->> x
         (cons {})
         (partition 2 1)
         (reduce (fn [agg [prev curr]] (concat agg (list {:prev prev :curr curr})))
                 ())
         ))

  fake-state

  (type {:a 2})

  (type (select-keys {:a 2} [:a]))

  (type (into {} (map #(do [% %]) (range 20))))

  (into {} (map #(do [% %]) (range 20)))

  (let [m (apply hash-map (flatten (range 20)))]
    (filter (fn [[k v]] :boogers) m)
  )

  (mod 3 2)

  (mod 4 2)


  (hash-map (flatten (range 2)))

  (flatten (range 10))


  (hash-map :a 2 :b 3)

  fake-state

  (fetch-upload-verbs fake-state)

  (fetch-upload-verbs fake-state :disable-cache false)

  (let [x (fetch-upload-verbs fake-state :disable-cache false)]
    (-> x
        first
        :df-1975-verbs
        first
        ))

  (let [[new-state err]
        (fetch-upload-verbs fake-state :disable-cache false)
        verb-warnings
        (get-in new-state [:warnings :df-1975-verbs])]
    (count verb-warnings))

  (first {:a 2 :b 3})

  (boolean (and nil 1))

  (update {:a 2} {:a 4})

  (filter #(= (:a %) 2) (list {:a 2} {:a 3} {:a 2 :b 3}))

  (string/join (take 80 (repeat \-)))

  (seq {})

  (let [a :a]
    (get {:a 2} a)
    )

  (map inc [1 2 3])

  (conj '(1 2 3) 4)

  (conj [1 2 3] 4)

  (type {:a 2})

  (filter (fn [t]
            (string/includes? (name (first t)) "translation"))
          {:some-translation "chien"
           :other "dog"})

  (filter (fn [[k v]] (even? k))
          {1 2 2 10 3 4 4 100})

  (filter identity (vals {:verb-type :root :a 2 :b 4}))

  (count (filter identity (vals {:verb-type :root :a 2 :b 4})))

  (count (filter identity (vals {:verb-type :root :a nil :b nil})))


  (filter first [[1 nil] [nil 1]])

  (assoc-in {} [:a :b] 2)

  (map :translations (fetch-upload-verbs fake-state false))

  (->> (fetch-upload-verbs fake-state false)
       (map second)
       (filter identity)
       ;; count
       )

  (or nil "")

  (->> {:a 2} :a)


  (->> (fetch-upload-verbs fake-state false)
       (map second)
       (filter identity)
       ;; count
       (map (fn [t] (get-in t [:err ::s/problems])))
       (group-by count)
       ;; ((fn [m] (get m 0)))
       )

  (filter
   #(get-in % [:b :b])
   '({:a {:b 2}})
   )

  (count (fetch-upload-verbs fake-state false))

  (filter identity (map :all-entries-key (fetch-upload-verbs fake-state false)))

  (let [kwixer (get-kwixer :prs-ʔ-grade)]
    (kwixer :surface-form))

  (let [kwixer (get-kwixer :prs-ʔ-grade)]
    (get-translation-keys kwixer))

  (let [kwixer (get-kwixer :prs-ʔ-grade)
        keys (get-translation-keys kwixer)]
    (get-translations prs-glot-test keys))

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

  (get-in {} [:a :b])

  (reverse [1 2 3])

)
