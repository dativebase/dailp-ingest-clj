(ns dailp-ingest-clj.utils-fiddle
  (:require [dailp-ingest-clj.utils :refer :all]
            [clojure.spec.gen.alpha :as gen]
            [clojure.set :refer [subset?]]
            [clojure.repl :refer [doc]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest])
  (:import java.util.Date))

(def test-keys [:a :b :c :a :a :b])

(def test-vals ["a 1" "b 1" "c val" "a 2" "a 3" "b 2"])

(def id-regex #"^[0-9]*$")

(s/conform even? 1000)

(s/conform even? 1001)

(s/valid? even? 1000)

(inst? (Date.))

(inst? 4)

(s/valid? even? 10001)

(s/def ::id int?)

(s/def ::id-regex
  (s/and
   string?
   #(re-matches id-regex %)))

(s/def ::id-types (s/or :int ::id :string ::id-regex))

(s/def ::name string?)

(s/def ::age int?)

(s/def ::skills list?)

(s/def ::developer (s/keys :req [::name ::age]
                                 :opt [::skills]))

(def suit? #{:heart :spade :club :diamond})

(def rank?
  (into #{:ace :king :queen :jack} (range 2 11)))

(def deck (for [s suit? r rank?] [s r]))

(def other-deck (mapcat identity (map (fn [s] (map (fn [r] [s r]) rank?)) suit?)))

(def aaa '[[c c c]
           [y y y]
           [m m m]])

(def bbb '[[r g b]
           [r g b]
           [r g b]])

(def ccc '[[x y z]
           [y z x]
           [z x y]])

(s/def ::port number?)

(s/def ::host string?)

(s/def ::id keyword?)

(s/def ::server (s/keys* :req [::id ::host] :opt [::port]))



(defn our-inc
  [n] [(inc n) nil])

(defn our-dec
  [n] [(dec n) nil])

(defn our-fail
  [n] [nil "err"])


(comment

  (macroexpand '(err->> [1 nil] our-inc our-dec))

  (macroexpand-1 '(err->> [1 nil] our-inc our-dec))

  (apply-or-error inc [1 nil])

  (apply-or-error inc [nil "i am error"])

  (err->> 1 our-inc our-dec)

  (err->> 1 our-inc our-inc)

  (err->> 1 our-inc our-inc our-fail)

  (err->> 1 our-fail our-inc our-inc)

  (char? \b)

  (char? "b")

  (contains? [1] 0)

  (contains? [1] 1)

  (let [x {1 2}]
    (contains? x 1))

  (partition 3 1 "underfunded")

  (nthrest [1 2 3] 3)

  (doall (take 2 (range 10)))

  (cons 1 '(1 2 3))

  (rest [1 2 3])

  ((set "abc") \a)

  ((set "abc") \z)

  (doc starts-with-any?)

  (:doc (meta #'starts-with-any?))

  (s/exercise-fn `starts-with-any?)
 
  (stest/instrument `starts-with-any?)

  (stest/check `starts-with-any?)

  (stest/check `ends-with-any?)

  (-> (starts-with-any? "abc" "aaa")
      set
      count
      )

  (last "abc")

  (#(-> % nil?) 1)

  (subset? #{\a \b} #{\a \b \c})

  (subset? #{\z \b} #{\a \b \c})

  (count (set (starts-with-any? "dog" "ddd")))

  (first (set (starts-with-any? "dog" "ddd")))

  (first "dog")

  (seq (list 1 2))

  (seq ())

  (seq? (seq (list 1 2)))

  (seq? (seq ()))

  (long (rand 10))

  (s/conform ::server [::id :s1 ::host "example.com" ::port 5555])

  (<= 1 4 3)

  (= :a :a)

  (= ::a :a)

  ::a

  :a

  :org.dative.old/abc


  '[a bc]

  (first '[a bc])

  (type (first '[a bc]))

  (get {1 2} 1)

  (boolean 1)

  (boolean 2)

  (boolean false)
  (boolean false)

  (mapv (partial mapv vector) aaa bbb ccc) 

  ((partial mapv vector) [1 2 3] [4 5 6] [7 8 9])

  (map reverse [[1 2 3] [3 2 1]])
  ;; ((3 2 1) (1 2 3))

  (mapcat reverse [[1 2 3] [3 2 1]])
  ;; (3 2 1 1 2 3)

  deck

  other-deck

  (= deck other-deck)

  (interleave [1 2 3] [4 5 6])
  ;; (1 4 2 5 3 6)

  (partition 2 (interleave [1 2 3] [4 5 6]))
  ;; ((1 4) (2 5) (3 6))

  (mapcat identity (partition 2 (interleave [1 2 3] [4 5 6])))
  ;; (1 4 2 5 3 6)

  (flatten (partition 2 (interleave [1 2 3] [4 5 6])))
  ;; (1 4 2 5 3 6)

  (mapv identity (list 1 2 3))

  (map identity (list 1 2 3))

  (mapv vector (list 1 2 3))
 


  (= '[1 2] [1 2])

  (suit? 1)

  (suit? :heart)

  (rank? :heart)

  (rank? :king)

  (rank? 2)

  (rank? 1)

  (#{1 2} 4)

  (gen/generate (s/gen int?))

  (gen/generate (s/gen ::developer))

  (nat-int? 1000000000000000000000000000000000000)

  (nat-int? -1)



  (zipmapgroup test-keys test-vals)
  ;; {:a ("a 1" "a 2" "a 3"), :b ("b 1" "b 2"), :c "c val"}

  (s/valid? ::developer {::name "Brad" ::age 24 ::skills '()})

  (s/valid? ::developer {::name "Brad" ::age 24 ::skills '("Clojure")})

  (s/valid? ::developer {::age 24 ::skills '()})

  (s/explain-str ::developer {::age 24 ::skills '()})

  (s/valid? ::id-types "12345")

  (s/valid? ::id-types "123a45")

  (s/valid? ::id-types 12345)

  (s/valid? ::id 12345)

  (s/valid? ::id 1)

  (s/valid? ::id-types "1")

  (s/valid? ::id-types 1)

  (s/valid? ::id-types 1.2)

  (* 8 8)

  (some #{1} '(1 2 3))

  (some #{4} '(1 2 3))

  ((fn [v]
    (cond
      (some #{v} '(:fut)) [:ppp-morpheme-break-1 :ppp-tag-1
                           :ppp-morpheme-break-2 :ppp-tag-2]
      (some #{v} '(:prs :impf :fut-impt)) []
      :else [:ppp-morpheme-break :ppp-tag]
    )
  ) :fut)


)

