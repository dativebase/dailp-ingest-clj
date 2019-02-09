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

(comment
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

