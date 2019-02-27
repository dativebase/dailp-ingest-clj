(ns dailp-ingest-clj.prepronominal-prefixes-fiddle
  "For playing with/testing the prepronominal_prfixes.clj functionality"
  (:require [dailp-ingest-clj.prepronominal-prefixes :refer :all]
            [dailp-ingest-clj.old-io :refer :all]
            [dailp-ingest-clj.utils :refer [seq-rets->ret]]
            [old-client.core :refer [make-old-client]]
            [old-client.forms :refer :all]
            [old-client.resources :refer :all]
            [clojure.string :as string]))

(defn fetch-ppps
  "Fetch all forms with syntactic category 'PPP'."
  []
  (search-forms
   (make-old-client)
   {:query {:filter ["Form" "syntactic_category" "name" "=" "PPP"]}}))

(defn delete-ppps
  "Delete all prepronominal prefix forms in the OLD instance. Return a 2-element
  attempt vector."
  []
  (let [oc (make-old-client)]
    (seq-rets->ret
     (map (fn [ppp]
            (delete-resource oc :form (:id ppp))
            [(format "Deleted syntactic category '%s'." (:name ppp)) nil])
          (fetch-ppps)))))

;; Fake state: so we don't have to run the previous ingest steps just to get
;; the tags and syntactic categories in the state.
(def fake-state
  {:old-client (make-old-client)
   :created_pronominal_prefixes [],
   :warnings {},
  :tags
  {:ppp-pre-consonantal
  {:id 376,
    :name "ppp-pre-consonantal",
    :description "Pre-consonantal prepronominal prefix",
    :datetime_modified "2019-02-26T20:41:57.295822"},
  :ingest-tag
  {:id 371,
    :name "ingest-uchihara-root:2019-02-26T20:41:57.111Z",
    :description
    "The tag for the data ingest that occurred at 2019-02-26T20:41:57.111Z.",
    :datetime_modified "2019-02-26T20:41:57.147946"},
  :mod-pre-v
  {:id 384,
    :name "mod-pre-v",
    :description "Modal suffix allomorphs before the vowel /v/.",
    :datetime_modified "2019-02-26T20:41:57.590117"},
  :refl-pre-h-s
  {:id 381,
    :name "refl-pre-h-s",
    :description
    "Reflexive or middle prefix allomorphs before the segments /h/ or /s/.",
    :datetime_modified "2019-02-26T20:41:57.497028"},
  :pp-pre-vocalic
  {:id 372,
    :name "pp-pre-vocalic",
    :description "Pre-vocalic pronominal prefix",
    :datetime_modified "2019-02-26T20:41:57.175963"},
  :mod-pre-vocalic
  {:id 383,
    :name "mod-pre-vocalic",
    :description "Modal suffix allomorphs before vowels.",
    :datetime_modified "2019-02-26T20:41:57.566031"},
  :refl-pre-a
  {:id 380,
    :name "refl-pre-a",
    :description "Reflexive or middle prefix allomorphs before the vowel /a/.",
    :datetime_modified "2019-02-26T20:41:57.461553"},
  :cl-pre-consonantal
  {:id 385,
    :name "cl-pre-consonantal",
    :description "Clitic allomorphs before consonants.",
    :datetime_modified "2019-02-26T20:41:57.611886"},
  :pp-pre-consonantal
  {:id 373,
    :name "pp-pre-consonantal",
    :description "Pre-consonantal pronominal prefix",
    :datetime_modified "2019-02-26T20:41:57.205501"},
  :cl-pre-vocalic
  {:id 386,
    :name "cl-pre-vocalic",
    :description "Clitic allomorphs before vowels.",
    :datetime_modified "2019-02-26T20:41:57.646856"},
  :pp-pre-v
  {:id 374,
    :name "pp-pre-v",
    :description
    "Pre-v pronominal prefix. This tag marks the \"3SG.B\" allomorph \"uwa-\" of pre-consonantal \"uu-\" (and pre-vocalic \"uw-\"). It occurs before stem-initial /v-/, which is a schwa-like vowel in Cherokee.",
    :datetime_modified "2019-02-26T20:41:57.234957"},
  :cl-pre-v
  {:id 387,
    :name "cl-pre-v",
    :description "Clitic allomorphs before the vowel /v/.",
    :datetime_modified "2019-02-26T20:41:57.670970"},
  :refl-pre-vocalic
  {:id 379,
    :name "refl-pre-vocalic",
    :description "Reflexive or middle prefix allomorphs before vowels.",
    :datetime_modified "2019-02-26T20:41:57.416537"},
  :mod-pre-consonantal
  {:id 382,
    :name "mod-pre-consonantal",
    :description "Modal suffix allomorphs before consonants.",
    :datetime_modified "2019-02-26T20:41:57.526872"},
  :refl-pre-consonantal
  {:id 378,
    :name "refl-pre-consonantal",
    :description "Reflexive or middle prefix allomorphs before consonants.",
    :datetime_modified "2019-02-26T20:41:57.375049"},
  :ppp-elsewhere
  {:id 377,
    :name "ppp-elsewhere",
    :description
    "Elsewhere prepronominal prefix. Prepronominal prefix allomorphs marked with this tag (e.g., /too/) occur before the CISL1, CISL2, and ITER1 prepronominal prefixes.",
    :datetime_modified "2019-02-26T20:41:57.338998"},
  :ppp-pre-vocalic
  {:id 375,
    :name "ppp-pre-vocalic",
    :description "Pre-vocalic prepronominal prefix",
    :datetime_modified "2019-02-26T20:41:57.256394"}},
  :syntactic-categories
  {:V
  {:id 65,
    :name "V",
    :type nil,
    :description "Verbs",
    :datetime_modified "2019-02-26T20:41:59.258259"},
  :S
  {:id 66,
    :name "S",
    :type "sentential",
    :description "Sentences",
    :datetime_modified "2019-02-26T20:41:59.284799"},
  :PP
  {:id 67,
    :name "PP",
    :type nil,
    :description "Pronominal Prefixes",
    :datetime_modified "2019-02-26T20:41:59.316391"},
  :PPP
  {:id 68,
    :name "PPP",
    :type nil,
    :description "Prepronominal Prefixes",
    :datetime_modified "2019-02-26T20:41:59.340015"},
  :REFL
  {:id 69,
    :name "REFL",
    :type nil,
    :description "Category for reflexive (REFL) and middle (MID) prefixes",
    :datetime_modified "2019-02-26T20:41:59.378233"},
  :MOD
  {:id 70,
    :name "MOD",
    :type nil,
    :description "Category for modal (MOD) suffixes",
    :datetime_modified "2019-02-26T20:41:59.409461"},
  :CL
  {:id 71,
    :name "CL",
    :type nil,
    :description "Category for clitics (CL)",
    :datetime_modified "2019-02-26T20:41:59.434783"}},
  :sources {},
  :orthographies
  {586
  {:name "taoc modified community",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, ?, ?, ?, ?, ?, ?, a:, e:, i:, o:, u:, v:, á:, é:, í:, ó:, ú:, v́:, ǎ:, ě:, ǐ:, ǒ:, ǔ, v̌:, â:, ê:, î:, ô, û:, v̂:, à:, è:, ì:, ò:, ù:, v̀:, a̋:, e̋:, i̋:, ő:, ű:, v̋:, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.420011",
    :id 586},
  587
  {:name "cherokee narratives",
    :orthography
    "a̱, e̱, i̱, o̱, u̱, v̱, a̱³, e̱³, i̱³, o̱³, u̱³, v̱³, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, a, e, i, o, u, v, a³, e³, i³, o³, u³, v³, a²³, e²³, i²³, o²³, u²³, v²³, a³², e³², i³², o³², u³², v³², a¹, e¹, i¹, o¹, u¹, v¹, a⁴, e⁴, i⁴, o⁴, u⁴, v⁴, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.447595",
    :id 587},
  588
  {:name "cherokee tone project",
    :orthography
    "ā, ē,  ī, ō,  ū,  v̄, á, é, í, ó, ú, v́, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ā:, ē:,  ī:, ō:,  ū:,  v̄:, á:, é:, í:, ó:, ú:, v́:, ǎ:, ě:, ǐ:, ǒ:, ǔ, v̌:, â:, ê:, î:, ô, û:, v̂:, à:, è:, ì:, ò:, ù:, v̀:, a̋:, e̋:, i̋:, ő:, ű:, v̋:, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.475226",
    :id 588},
  589
  {:name "taoc",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, a̋, e̋, i̋, ő, ű, v̋, aa, ee, ii, oo, uu, vv, áá, éé, íí, óó, úú, v́v́, aá, eé, ií, oó, uú, vv́, áa, ée, íi, óo, úu, v́v, àà, èè, ìì, òò, ùù, v̀v̀, aa̋, ee̋, ii̋, oő, uű, vv̋, V, t, th , k, kh , kw, kwh, c , ch, tl, tlh, s, h, ʔ, l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.509989",
    :id 589},
  590
  {:name "dailp",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, a̋, e̋, i̋, ő, ű, v̋, aa, ee, ii, oo, uu, vv, áá, éé, íí, óó, úú, v́v́, aá, eé, ií, oó, uú, vv́, áa, ée, íi, óo, úu, v́v, àà, èè, ìì, òò, ùù, v̀v̀, aa̋, ee̋, ii̋, oő, uű, vv̋, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.533521",
    :id 590},
  591
  {:name "uchihara database",
    :orthography
    "a, e, i, o, u, v, a!, e!, i!, o!, u!, v!, a`, e`, i`, o`, u`, v`, a\" , e\" , i\" , o\" , u\" , v\" , a:, e:, i:, o:, u:, v:, a:!, e:!, i:!, o:!, u:!, v:!, a:*, e:*, i:*, o:*, u:*, v:*, a:^, e:^, i:^, o:^, u:^, v:^, a:`, e:`, i:`, o:`, u:`, v:`, a:\" , e:\" , i:\" , o:\" , u:\" , v:\" , V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ', l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.558941",
    :id 591},
  592
  {:name "feeling 1975",
    :orthography
    "ạ², ẹ², ị², ọ², ụ², ṿ², ạ³, ẹ³, ị³, ọ³, ụ³, ṿ³, ạ¹, ẹ¹, ị¹, ọ¹, ụ¹, ṿ¹, ạ⁴, ẹ⁴, ị⁴, ọ⁴, ụ⁴, ṿ⁴, a², e², i², o², u², v², a³, e³, i³, o³, u³, v³, a²³, e²³, i²³, o²³, u²³, v²³, a³², e³², i³², o³², u³², v³², a¹, e¹, i¹, o¹, u¹, v¹, a⁴, e⁴, i⁴, o⁴, u⁴, v⁴, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.590881",
    :id 592},
  593
  {:name "crg",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, ?, ?, ?, ?, ?, ?, aa, ee, ii, oo, uu, vv, áa, ée, íi, óo, úu, v́v, aá, eé, ií, oó, uú, vv́, áà, éè, íì, óò, úù, v́v̀, aà, eè, iì, oò, uù, vv̀, áá, éé, íí, óó, úú, v́v́, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m",
    :lowercase false,
    :initial_glottal_stops true,
    :datetime_modified "2019-02-26T20:41:54.615383",
    :id 593}}})

(comment

  (delete-ppps)  ;; in the OLD instance

  (fetch-ppps)  ;; from the OLD instance

  (fetch-ppps-from-worksheet :disable-cache false)

  (fetch-ppps-from-worksheet :disable-cache true)

  (construct-ppp-form-maps :state fake-state)

  (fetch-upload-ppp-forms fake-state :disable-cache false)

)
