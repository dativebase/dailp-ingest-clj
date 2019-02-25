(ns dailp-ingest-clj.utils
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as spec]
            [clojure.set :refer [subset?]]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [clojure.data.csv :as csv]))

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

(defn weird->hyphen
  "Replace all 'weird' chars with a hyphen."
  [thing]
  (string/replace thing #"(_|\s)+" "-"))

(defn clean-for-kw
  "Clean an externally generated string so that it can be used as a keyword."
  [str-th]
  (-> str-th
      string/trim
      string/lower-case
      (string/replace #"," "")
      weird->hyphen))

(defn str->kw
  [thing] (-> thing
              clean-for-kw
              keyword))

(comment

  (weird->hyphen "ab_ c d  ")

  (str->kw "ab_ c d  ")

)

;; See https://adambard.com/blog/acceptable-error-handling-in-clojure/."
(defn apply-or-error
  [f [val err]]
  (if (nil? err)
    (f val)
    [nil err]))

;; See https://adambard.com/blog/acceptable-error-handling-in-clojure/."
(defmacro err->> [val & fns]
  (let [fns (for [f fns] `(apply-or-error ~f))]
    `(->> [~val nil]
          ~@fns)))

(defn seq-rets->ret
  "Given a sequence of return values, return a single return value. The return
  values are assumed to be 2-tuple vectors where the first element is a
  success value and the second is an error message. Success has an error
  message of nil, failure has a success value of nil. See
  https://adambard.com/blog/acceptable-error-handling-in-clojure/."
  [rets]
  (let [errs (filter some? (map second rets))]
    (if (seq errs) [nil errs] [(map first rets) nil])))

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

(comment

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

)
