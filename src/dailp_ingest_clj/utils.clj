(ns dailp-ingest-clj.utils
  (:require [clojure.string :as string]))

(defn starts-with-any?
  "Return truthy if s starts with any character in chrs; otherwise nil."
  [s chrs]
  (seq (filter (fn [c] (string/starts-with? s (str c))) chrs)))

(defn ends-with-any?
  "Return truthy if s ends with any character in chrs; otherwise nil."
  [s chrs]
  (seq (filter (fn [c] (string/ends-with? s (str c))) chrs)))

(true? 1)

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

(defn whitespace->hyphen
  "Replace all whitespace chars with a hyphen."
  [thing]
  (string/replace thing #"\s+" "-"))

(defn clean-for-kw
  "Clean an externally generated string so that it can be used as a keyword."
  [str-th]
  (-> str-th
      string/trim
      (string/replace #"," "")
      whitespace->hyphen))

(defn str->kw
  [thing] (-> thing
              clean-for-kw
              keyword))

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

)
