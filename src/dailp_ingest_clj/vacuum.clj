(ns dailp-ingest-clj.vacuum)


(def instructions
  "Each stroke of a vacuum pump extracts 5% of the air in a 50-m^3 tank. How
  much air is removed after 50 strokes (to nearest tenth)?")

(defn stroke
  [vol] (* 0.95 vol))

(defn stroke-n-times
  [n vol]
  (if (= 0 n)
    vol
    (stroke-n-times (dec n) (stroke vol))))

(let [vol 50
      strokes 50]
  (- vol (stroke-n-times 50 50)))

(comment

  instructions

  (stroke 50)

  (stroke 47.5)

  (stroke-n-times 1 50)

  (stroke-n-times 2 50)

  (stroke-n-times 50 50)

)
