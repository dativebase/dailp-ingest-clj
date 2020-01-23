(ns dailp-ingest-clj.utils
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as spec]
            [clojure.set :refer [subset?]]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [clojure.data.csv :as csv]
            [clojure.string :as str]))

(s/fdef starts-with-any?
  :args (s/cat :s string? :chrs string?)
  :ret (s/nilable (s/coll-of char?))
  :fn (s/or :nil-case #(-> % :ret nil?)
            :non-nil-case
            (s/and #(= (-> % :ret set first) (-> % :args :s first))
                   #(= 1 (-> % :ret set count))
                   (fn [x] (subset? (-> x :ret set) (-> x :args :chrs set))))))

(defn starts-with-any?
  "Return truthy if s starts with any character in chrs; otherwise nil."
  [s chrs]
  (seq (filter (fn [c] (string/starts-with? s (str c))) chrs)))

(s/fdef ends-with-any?
  :args (s/cat :s string? :chrs string?)
  :ret (s/nilable (s/coll-of char?))
  :fn (s/or :nil-case #(-> % :ret nil?)
            :non-nil-case
            (s/and #(= (-> % :ret set first) (-> % :args :s last))
                   #(= 1 (-> % :ret set count))
                   (fn [x] (subset? (-> x :ret set) (-> x :args :chrs set))))))

(defn ends-with-any?
  "Return truthy if s ends with any character in chrs; otherwise nil."
  [s chrs]
  (seq (filter (fn [c] (string/ends-with? s (str c))) chrs)))

(defn l-strip
  "Left-strip all chars in pfx from s."
  [s pfx]
  (if (starts-with-any? s pfx)
    (l-strip (apply str (drop 1 s)) pfx)
    s))

(defn r-strip
  "Right-strip all chars in sfx from s."
  [s sfx]
  (if (ends-with-any? s sfx)
    (r-strip (apply str (take (dec (count s)) s)) sfx)
    s))

(defn strip
  "Strip all chars in chrs from s."
  [s chars]
  (-> s
      (r-strip chars)
      (l-strip chars)))

(defn trimnil
  [thing]
  (try (string/trim thing)
       (catch java.lang.NullPointerException _ nil)))

(defn whitespace->hyphen
  "Replace all whitespace chars with a hyphen."
  [thing]
  (string/replace thing #"\s+" "-"))

(defn map-hyphens->underscores
  [m]
  (->> m
       (map (fn [[k v]]
              [(if (keyword? k) (-> k name (str/replace #"-" "_") keyword) k) v]))
       (into {})))

(defn weird->hyphen
  "Replace all 'weird' chars with a hyphen."
  [thing]
  (string/replace thing #"(\.|_|\s)+" "-"))

(defn clean-for-kw
  "Clean an externally generated string so that it can be used as a keyword."
  [str-th]
  (-> str-th
      string/trim
      string/lower-case
      (string/replace #"[\(\),:]" "")
      (string/replace #"/" "-")
      weird->hyphen
      (strip "-")))

(defn str->kw
  [thing] (-> thing
              clean-for-kw
              keyword))

(defn empty-str-or-nil->nil
  [thing]
  (if (nil? thing)
    nil
    (let [val (string/trim thing)]
      (if (seq val) val nil))))

(comment

  (map-hyphens->underscores {:a-b 2 :c_d 3 :d 5 "abc" 44})

  (map-hyphens->underscores {:a-b 2 :a_b 44})

  (= (->> {:a 2 :b 3 :c nil :d false :e 0 :f ""}
          (filter second)
          (into {}))
     (->> {:a 2 :b 3 :c nil :d false :e 0 :f ""}
          (filter (fn [[k v]] v))
          (into {})))

  (let [rows [["a" nil] [nil " "]]]
    (->> (take 2 rows)
         (map (fn [row] (map empty-str-or-nil->nil row)))
         ;; (map (fn [row] row))
         ;; (filter #(complement (every? nil? %)))
         (filter #(not (every? nil? %)))
    )
  )

  (empty-str-or-nil->nil nil)

  (empty-str-or-nil->nil "  \n \t ")

  (empty-str-or-nil->nil "  n \t ")

  (map empty-str-or-nil->nil (list nil "\n\t" "  n \t "))


  (weird->hyphen "ab_ c d  ")

  (str->kw "ab_ c d  ")

  (assoc {} :a 2)

  ;; functor
  ;; (map (fn [x] x) (list 1 2 3))
  ;; ((a -> b) (f a)) -> (f b)

  ;; applicative functor
  ;; ((a -> b) (f a)) -> (f b)

  ;; monad
  ;; ((a -> (f b)) (f a)) -> (f b)

)

(defn just
  [x]
  [x nil])

(defn nothing
  [error]
  [nil error])

(defn just-then
  "Given a maybe (`[just error]`) and a then function that returns a value given
  the `just` as input, return that value, or a nothing (`[nil error]`) if
  `error` is truthy."
  ([maybe then-fn] (just-then maybe then-fn identity))
  ([[val error] then-fn error-fn]
   (if error
     (nothing (error-fn error))
     (just (then-fn val)))))

;; See https://adambard.com/blog/acceptable-error-handling-in-clojure/."
(defn apply-or-error
  "Call f on val if err is nil, otherwise return [nil err]
  See https://adambard.com/blog/acceptable-error-handling-in-clojure/."
  [f [val err]]
  (if (nil? err)
    (f val)
    (nothing err)))

(def bind apply-or-error)

(defmacro err->>
  [val & fns]
  `(->> [~val nil]
        ~@(map (fn [f]
                 `(bind ~f))
               fns)))

;; See https://adambard.com/blog/acceptable-error-handling-in-clojure/."
(defmacro orig-err->> [val & fns]
  (let [fns (for [f fns] `(apply-or-error ~f))]
    `(->> [~val nil]
          ~@fns)))

(defn maybes->maybe
  "Given a sequence of maybes, return a single maybe."
  [rets]
  (let [err (->> rets (map second) (filter some?) first)]
    (if err
      (nothing err)
      (just (map first rets)))))

(def seq-rets->ret maybes->maybe)

(defn zipmapgroup
  "Returns a map with the keys mapped to the corresponding vals. If a key
  already exists, change the existing val to a list of that val (if necessary)
  and append the new val. Like zipmap with group-by."
  [keys vals]
  (->> (map vector keys vals)
       (group-by first)
       (map (fn [[k v]]
              (let [val (map second v)]
                (if (= (count val) 1) [k (first val)] [k val]))))
       (into {})))

(defn table->sec-of-maps
  "Convert a vector of vectors of strings (output of google-io/fetch-worksheet)
  to a seq of maps; assumes first vector of strings is the header row of the
  CSV, which supplies keys for the resulting maps. Duplicate column names result
  in maps whose values are vectors."
  [csv-data]
  (map zipmapgroup
       (->> (first csv-data) ;; First row is the header
            (map (fn [x] (str->kw x)))
            repeat)
       (rest csv-data)))

(defn read-csv-io
  "Read the CSV file at csv-path, producing a lazy vector of strings, and
  return the result of running pure-processor on the input lazy vector."
  [csv-path pure-processor]
  (with-open [reader (io/reader csv-path)]
    (->> (csv/read-csv reader)
         pure-processor)))

(defn remove-nils
  "Remove nils from seqthingy."
  [seqthingy]
  (filter identity seqthingy))

(defn kw->human
  "Convert a keyword like :Dog-Cat to `Dog Cat`."
  [kw]
  (-> kw
      name
      (string/replace "-" " ")))

(defn exp
  [x n]
  (reduce * (repeat n x)))

(defn base26->10
  "Converts a string like AA to a base-26 number:
  A   => 1
  B   => 2
  Z   => 26
  AA  => 27
  AB  => 28
  ABC => 731 (1 * 26^2 + 2 * 26^1 + 3 + 26^0)"
  [b26]
  (->> b26
       str/lower-case
       reverse
       (map-indexed (fn [i e] (* (exp 26 i) (- (int e) 96))))
       (reduce +)))

(defn full-stop
  [s]
  (if (re-find #"[\.\?!]$" s)
    s
    (str s ".")))

(comment

  (full-stop "abc!")

  (seq-rets->ret (list))

  (seq-rets->ret (list [1 nil] [2 nil]))

  (seq-rets->ret (list [nil "err thing"] [2 nil]))

  (starts-with-any? "abcde" "ab")

  (ends-with-any? "abcde" "eb")

  (ends-with-any? "abcde" "zb")

  (r-strip "ababdogsbbbbaa" "ba")

  (r-strip "ababdogsbbbbaa" "aa")

  (l-strip "ababdogsbbbbaa" "aa")

  (l-strip "ababdogsbbbbaa" "ab")

  (strip "ababdogsbbbbaa" "ab")

  (strip "\"  dogs\n\n\n" " \n\"")

  (string/trim " a ")

  (string/trim nil)

  (trimnil nil)

  (trimnil " a ")

  (string/join ", " (list "a" "b"))

  (group-by second [[nil 1] [1 nil]])


)
