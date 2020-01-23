(ns dailp-ingest-clj.pronominal-prefixes
  "Logic for ingesting DAILP pronominal prefixes (PPs) from the Google Sheets at
  - Sets A & B Pronominal Prefixes:
    https://docs.google.com/spreadsheets/d/1D0JZEwE-dj-fKppbosaGhT7Xyyy4lVxmgG02tpEi8nw/edit#gid=0
  - Combined Pronominal Prefixes:
    https://docs.google.com/spreadsheets/d/1OMzkbDGY1BqPR_ZwJRe4-F5_I12Ao5OJqqMp8Ej_ZhE/edit?usp=sharing
  - Reflexive & Middle Pronominal Prefixes:
    https://docs.google.com/spreadsheets/d/1Q_q_1MZbmZ-g0bmj1sQouFFDnLBINGT3fzthPgqgkqo/edit?usp=sharing."
  (:require [dailp-ingest-clj.affixes :refer [affix-map->seq-of-forms
                                              construct-affix-form-maps]]
            [dailp-ingest-clj.google-io :refer [fetch-worksheet-caching]]
            [dailp-ingest-clj.resources :refer [upsert-resource]]
            [dailp-ingest-clj.utils :refer [apply-or-error
                                            seq-rets->ret
                                            table->sec-of-maps]]))

(def c-pps-sheet-name "Combined Pronominal Prefixes")
(def c-pps-worksheet-name "Sheet1")
(def c-pps-max-col 13)
(def c-pps-max-row 27)

(def ab-pps-sheet-name-DEPRECATED "Sets A & B Pronominal Prefixes")
(def ab-pps-sheet-name "Pronominal Prefixes--Sets A & B")
(def ab-pps-worksheet-name "Sheet1")
(def ab-pps-max-col 15)
(def ab-pps-max-row 30)

(def rm-pps-sheet-name "Reflexive & Middle Pronominal Prefixes")
(def rm-pps-worksheet-name "Sheet1")
(def rm-pps-max-col 16)
(def rm-pps-max-row 3)

(defn fetch-c-pps-from-worksheet
  "Fetch the COMBINED pronominal prefixes from the Google Sheets worksheet."
  [& {:keys [disable-cache] :or {disable-cache true}}]
   [(->> (fetch-worksheet-caching {:spreadsheet-title c-pps-sheet-name
                                   :worksheet-title c-pps-worksheet-name
                                   :max-col c-pps-max-col
                                   :max-row c-pps-max-row}
                                  disable-cache)
         table->sec-of-maps) nil])

(defn fetch-ab-pps-from-worksheet
  "Fetch the Sets A & B pronominal prefixes from the Google Sheets worksheet."
  [& {:keys [disable-cache] :or {disable-cache true}}]
  [(->> (fetch-worksheet-caching {:spreadsheet-title ab-pps-sheet-name
                                  :worksheet-title ab-pps-worksheet-name
                                  :max-col ab-pps-max-col
                                  :max-row ab-pps-max-row}
                                 disable-cache)
        table->sec-of-maps) nil])

(defn fetch-rm-pps-from-worksheet
  "Fetch the Reflexive & Middle pronominal prefixes from the Google Sheets
  worksheet."
  [& {:keys [disable-cache] :or {disable-cache true}}]
  [(->> (fetch-worksheet-caching {:spreadsheet-title rm-pps-sheet-name
                                  :worksheet-title rm-pps-worksheet-name
                                  :max-col rm-pps-max-col
                                  :max-row rm-pps-max-row}
                                 disable-cache)
        table->sec-of-maps) nil])

(defn construct-c-pp-form-maps
  "Return an either whose value is a map whose keys are :state and
  :pp-form-maps. The state value may have warnings added to it. The
  PP prefix value should be a seq of form maps representing Combined
  pronominal prefixes."
  [state c-pps]
  (construct-affix-form-maps state c-pps :c-pp-form-maps :PP))

(defn construct-ab-pp-form-maps
  "Return an either whose value is a map whose keys are :state and
  :ab-pp-form-maps. The state value may have warnings added to it. The
  PP prefix value should be a seq of form maps representing sets A & B
  pronominal prefixes."
  [state ab-pps]
  (construct-affix-form-maps state ab-pps :ab-pp-form-maps :PP))

(defn construct-rm-pp-form-maps
  "Return an either whose value is a map whose keys are :state and
  :rm-pp-form-maps. The state value may have warnings added to it. The
  PP prefix value should be a seq of form maps representing Reflexive & Middle
  pronominal prefixes."
  [state rm-pps]
  (construct-affix-form-maps state rm-pps :rm-pp-form-maps :PP))

(defn upload-c-pps 
  "Upload the seq of PP form resource maps (pps) to an OLD instance."
  [{:keys [state c-pp-form-maps]}]
  (apply-or-error
   (fn [pp-forms] [{:state state :c-pp-forms pp-forms} nil])
   (seq-rets->ret
    (map (fn [pp] (upsert-resource state pp :resource-name :form))
         c-pp-form-maps))))

(defn upload-ab-pps 
  "Upload the seq of PP form resource maps (pps) to an OLD instance."
  [{:keys [state ab-pp-form-maps]}]
  (apply-or-error
   (fn [pp-forms] [{:state state :ab-pp-forms pp-forms} nil])
   (seq-rets->ret
    (map (fn [pp] (upsert-resource state pp :resource-name :form))
         ab-pp-form-maps))))

(defn upload-rm-pps 
  "Upload the seq of Reflexive/Middle PP form resource maps (pps) to an OLD instance."
  [{:keys [state rm-pp-form-maps]}]
  (apply-or-error
   (fn [pp-forms] [{:state state :rm-pp-forms pp-forms} nil])
   (seq-rets->ret
    (map (fn [pp] (upsert-resource state pp :resource-name :form))
         rm-pp-form-maps))))

(defn update-state-c-pp-forms
  [{:keys [state c-pp-forms]}]
  [(update state :pp-forms
           (partial merge
                    (into {} (map (fn [f] [(:id f) f]) c-pp-forms)))) nil])

(defn update-state-ab-pp-forms
  [{:keys [state ab-pp-forms]}]
  [(update state :pp-forms
           (partial merge
                    (into {} (map (fn [f] [(:id f) f]) ab-pp-forms)))) nil])

(defn update-state-rm-pp-forms
  [{:keys [state rm-pp-forms]}]
  [(update state :pp-forms
           (partial merge
                    (into {} (map (fn [f] [(:id f) f]) rm-pp-forms)))) nil])

(defn fetch-upload-c-pp-forms
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-c-pps-from-worksheet :disable-cache disable-cache)
       (apply-or-error (partial construct-c-pp-form-maps state))
       (apply-or-error upload-c-pps)
       (apply-or-error update-state-c-pp-forms)))

(defn fetch-upload-ab-pp-forms
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-ab-pps-from-worksheet :disable-cache disable-cache)
       (apply-or-error (partial construct-ab-pp-form-maps state))
       (apply-or-error upload-ab-pps)
       (apply-or-error update-state-ab-pp-forms)))

(defn fetch-upload-rm-pp-forms
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-rm-pps-from-worksheet :disable-cache disable-cache)
       (apply-or-error (partial construct-rm-pp-form-maps state))
       (apply-or-error upload-rm-pps)
       (apply-or-error update-state-rm-pp-forms)
       ))

(defn fetch-upload-pp-forms
  "Fetch PP forms from GSheets, upload them to OLD, return state map with
   :created_pronominal_prefixes submap updated."
  [state & {:keys [disable-cache] :or {disable-cache true}}]
  (->> (fetch-upload-c-pp-forms state :disable-cache disable-cache)
       (apply-or-error
        (fn [state]
          (fetch-upload-ab-pp-forms state :disable-cache disable-cache)))
       (apply-or-error
        (fn [state]
          (fetch-upload-rm-pp-forms state :disable-cache disable-cache)))))
