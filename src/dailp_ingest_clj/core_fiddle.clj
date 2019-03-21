(ns dailp-ingest-clj.core-fiddle
  (:require [dailp-ingest-clj.core :refer [ingest]]
            [dailp-ingest-clj.old-io :refer [get-state]]
            [dailp-ingest-clj.utils :refer [apply-or-error err->>]]
            [dailp-ingest-clj.resources :refer [delete-everything]]))

(defn partishun
  "Returns a lazy sequence of lists of n items each, at offsets step
  apart. If step is not supplied, defaults to n, i.e. the partitions
  do not overlap. If a pad collection is supplied, use its elements as
  necessary to complete last partishun upto n items. In case there are
  not enough padding elements, return a partishun with less than n items."
  {:added "1.0"
   :static true}
  ([n coll]
   (partishun n n coll))
  ([n step coll]
   (lazy-seq
    (when-let [s (seq coll)]
      (let [p (doall (take n s))]
        (when (= n (count p))
          (cons p (partishun n step (nthrest s step))))))))
  ([n step pad coll]
   (lazy-seq
    (when-let [s (seq coll)]
      (let [p (doall (take n s))]
        (if (= n (count p))
          (cons p (partishun n step pad (nthrest s step)))
          (list (take n (concat p pad)))))))))


(comment

  (delete-everything (get-state))

  (ingest)

)

