(ns dailp-ingest-clj.modal-suffixes-fiddle
  (:require [dailp-ingest-clj.modal-suffixes :refer :all]
            [old-client.core :refer [make-old-client]]
            [old-client.forms :refer :all]
            [old-client.resources :refer :all]))

(defn fetch-mod-sfxs
  "Fetch all forms with syntactic category 'MOD'."
  []
  (search-forms
   (make-old-client)
   {:query {:filter ["Form" "syntactic_category" "name" "=" "MOD"]}}))

#_(def fake-state
  {:old-client (make-old-client)
  :created_pronominal_prefixes [],
  :warnings
  {:negative-PPP-omission-warnings
   (list "Omitting ingestion of NEG morpheme with first allomorph 'kee' glossed as 'NEG2'."
    "Omitting ingestion of NEG morpheme with first allomorph 'ka' glossed as 'NEG1'.")},
  :tags
  {:ppp-pre-consonantal
   {:id 410,
    :name "ppp-pre-consonantal",
    :description "Pre-consonantal prepronominal prefix",
    :datetime_modified "2019-02-27T00:55:24.215414"},
   :ingest-tag
   {:id 422,
    :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z",
    :description
    "The tag for the data ingest that occurred at 2019-02-27T02:27:36.768Z.",
    :datetime_modified "2019-02-27T02:27:36.825097"},
   :mod-pre-v
   {:id 418,
    :name "mod-pre-v",
    :description "Modal suffix allomorphs before the vowel /v/.",
    :datetime_modified "2019-02-27T00:55:24.454667"},
   :refl-pre-h-s
   {:id 415,
    :name "refl-pre-h-s",
    :description
    "Reflexive or middle prefix allomorphs before the segments /h/ or /s/.",
    :datetime_modified "2019-02-27T00:55:24.362334"},
   :pp-pre-vocalic
   {:id 406,
    :name "pp-pre-vocalic",
    :description "Pre-vocalic pronominal prefix",
    :datetime_modified "2019-02-27T00:55:24.113125"},
   :mod-pre-vocalic
   {:id 417,
    :name "mod-pre-vocalic",
    :description "Modal suffix allomorphs before vowels.",
    :datetime_modified "2019-02-27T00:55:24.422094"},
   :refl-pre-a
   {:id 414,
    :name "refl-pre-a",
    :description "Reflexive or middle prefix allomorphs before the vowel /a/.",
    :datetime_modified "2019-02-27T00:55:24.338025"},
   :cl-pre-consonantal
   {:id 419,
    :name "cl-pre-consonantal",
    :description "Clitic allomorphs before consonants.",
    :datetime_modified "2019-02-27T00:55:24.485973"},
   :pp-pre-consonantal
   {:id 407,
    :name "pp-pre-consonantal",
    :description "Pre-consonantal pronominal prefix",
    :datetime_modified "2019-02-27T00:55:24.134340"},
   :cl-pre-vocalic
   {:id 420,
    :name "cl-pre-vocalic",
    :description "Clitic allomorphs before vowels.",
    :datetime_modified "2019-02-27T00:55:24.509694"},
   :pp-pre-v
   {:id 408,
    :name "pp-pre-v",
    :description
    "Pre-v pronominal prefix. This tag marks the \"3SG.B\" allomorph \"uwa-\" of pre-consonantal \"uu-\" (and pre-vocalic \"uw-\"). It occurs before stem-initial /v-/, which is a schwa-like vowel in Cherokee.",
    :datetime_modified "2019-02-27T00:55:24.153445"},
   :cl-pre-v
   {:id 421,
    :name "cl-pre-v",
    :description "Clitic allomorphs before the vowel /v/.",
    :datetime_modified "2019-02-27T00:55:24.529429"},
   :refl-pre-vocalic
   {:id 413,
    :name "refl-pre-vocalic",
    :description "Reflexive or middle prefix allomorphs before vowels.",
    :datetime_modified "2019-02-27T00:55:24.308768"},
   :mod-pre-consonantal
   {:id 416,
    :name "mod-pre-consonantal",
    :description "Modal suffix allomorphs before consonants.",
    :datetime_modified "2019-02-27T00:55:24.398388"},
   :refl-pre-consonantal
   {:id 412,
    :name "refl-pre-consonantal",
    :description "Reflexive or middle prefix allomorphs before consonants.",
    :datetime_modified "2019-02-27T00:55:24.276564"},
   :ppp-elsewhere
   {:id 411,
    :name "ppp-elsewhere",
    :description
    "Elsewhere prepronominal prefix. Prepronominal prefix allomorphs marked with this tag (e.g., /too/) occur before the CISL1, CISL2, and ITER1 prepronominal prefixes.",
    :datetime_modified "2019-02-27T00:55:24.240612"},
   :ppp-pre-vocalic
   {:id 409,
    :name "ppp-pre-vocalic",
    :description "Pre-vocalic prepronominal prefix",
    :datetime_modified "2019-02-27T00:55:24.183499"}},
  :syntactic-categories
  {:V
   {:id 79,
    :name "V",
    :type nil,
    :description "Verbs",
    :datetime_modified "2019-02-27T00:55:25.955123"},
   :S
   {:id 80,
    :name "S",
    :type "sentential",
    :description "Sentences",
    :datetime_modified "2019-02-27T00:55:25.975584"},
   :PP
   {:id 81,
    :name "PP",
    :type nil,
    :description "Pronominal Prefixes",
    :datetime_modified "2019-02-27T00:55:26.015497"},
   :PPP
   {:id 82,
    :name "PPP",
    :type nil,
    :description "Prepronominal Prefixes",
    :datetime_modified "2019-02-27T00:55:26.053503"},
   :REFL
   {:id 83,
    :name "REFL",
    :type nil,
    :description "Category for reflexive (REFL) and middle (MID) prefixes",
    :datetime_modified "2019-02-27T00:55:26.084626"},
   :MOD
   {:id 84,
    :name "MOD",
    :type nil,
    :description "Category for modal (MOD) suffixes",
    :datetime_modified "2019-02-27T00:55:26.128288"},
   :CL
   {:id 85,
    :name "CL",
    :type nil,
    :description "Category for clitics (CL)",
    :datetime_modified "2019-02-27T00:55:26.156995"}},
  :sources {},
  :orthographies
  {602
   {:initial_glottal_stops true,
    :id 602,
    :lowercase false,
    :name "taoc modified community",
    :datetime_modified "2019-02-27T00:55:21.946418",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, ?, ?, ?, ?, ?, ?, a:, e:, i:, o:, u:, v:, á:, é:, í:, ó:, ú:, v́:, ǎ:, ě:, ǐ:, ǒ:, ǔ, v̌:, â:, ê:, î:, ô, û:, v̂:, à:, è:, ì:, ò:, ù:, v̀:, a̋:, e̋:, i̋:, ő:, ű:, v̋:, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m"},
   603
   {:initial_glottal_stops true,
    :id 603,
    :lowercase false,
    :name "cherokee narratives",
    :datetime_modified "2019-02-27T00:55:21.979976",
    :orthography
    "a̱, e̱, i̱, o̱, u̱, v̱, a̱³, e̱³, i̱³, o̱³, u̱³, v̱³, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, a, e, i, o, u, v, a³, e³, i³, o³, u³, v³, a²³, e²³, i²³, o²³, u²³, v²³, a³², e³², i³², o³², u³², v³², a¹, e¹, i¹, o¹, u¹, v¹, a⁴, e⁴, i⁴, o⁴, u⁴, v⁴, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m"},
   604
   {:initial_glottal_stops true,
    :id 604,
    :lowercase false,
    :name "cherokee tone project",
    :datetime_modified "2019-02-27T00:55:22.010444",
    :orthography
    "ā, ē,  ī, ō,  ū,  v̄, á, é, í, ó, ú, v́, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ā:, ē:,  ī:, ō:,  ū:,  v̄:, á:, é:, í:, ó:, ú:, v́:, ǎ:, ě:, ǐ:, ǒ:, ǔ, v̌:, â:, ê:, î:, ô, û:, v̂:, à:, è:, ì:, ò:, ù:, v̀:, a̋:, e̋:, i̋:, ő:, ű:, v̋:, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m"},
   605
   {:initial_glottal_stops true,
    :id 605,
    :lowercase false,
    :name "taoc",
    :datetime_modified "2019-02-27T00:55:22.037573",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, a̋, e̋, i̋, ő, ű, v̋, aa, ee, ii, oo, uu, vv, áá, éé, íí, óó, úú, v́v́, aá, eé, ií, oó, uú, vv́, áa, ée, íi, óo, úu, v́v, àà, èè, ìì, òò, ùù, v̀v̀, aa̋, ee̋, ii̋, oő, uű, vv̋, V, t, th , k, kh , kw, kwh, c , ch, tl, tlh, s, h, ʔ, l, y, w, m"},
   606
   {:initial_glottal_stops true,
    :id 606,
    :lowercase false,
    :name "dailp",
    :datetime_modified "2019-02-27T00:55:22.061952",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, a̋, e̋, i̋, ő, ű, v̋, aa, ee, ii, oo, uu, vv, áá, éé, íí, óó, úú, v́v́, aá, eé, ií, oó, uú, vv́, áa, ée, íi, óo, úu, v́v, àà, èè, ìì, òò, ùù, v̀v̀, aa̋, ee̋, ii̋, oő, uű, vv̋, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m"},
   607
   {:initial_glottal_stops true,
    :id 607,
    :lowercase false,
    :name "uchihara database",
    :datetime_modified "2019-02-27T00:55:22.088023",
    :orthography
    "a, e, i, o, u, v, a!, e!, i!, o!, u!, v!, a`, e`, i`, o`, u`, v`, a\" , e\" , i\" , o\" , u\" , v\" , a:, e:, i:, o:, u:, v:, a:!, e:!, i:!, o:!, u:!, v:!, a:*, e:*, i:*, o:*, u:*, v:*, a:^, e:^, i:^, o:^, u:^, v:^, a:`, e:`, i:`, o:`, u:`, v:`, a:\" , e:\" , i:\" , o:\" , u:\" , v:\" , V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ', l, y, w, m"},
   608
   {:initial_glottal_stops true,
    :id 608,
    :lowercase false,
    :name "feeling 1975",
    :datetime_modified "2019-02-27T00:55:22.119084",
    :orthography
    "ạ², ẹ², ị², ọ², ụ², ṿ², ạ³, ẹ³, ị³, ọ³, ụ³, ṿ³, ạ¹, ẹ¹, ị¹, ọ¹, ụ¹, ṿ¹, ạ⁴, ẹ⁴, ị⁴, ọ⁴, ụ⁴, ṿ⁴, a², e², i², o², u², v², a³, e³, i³, o³, u³, v³, a²³, e²³, i²³, o²³, u²³, v²³, a³², e³², i³², o³², u³², v³², a¹, e¹, i¹, o¹, u¹, v¹, a⁴, e⁴, i⁴, o⁴, u⁴, v⁴, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m"},
   609
   {:initial_glottal_stops true,
    :id 609,
    :lowercase false,
    :name "crg",
    :datetime_modified "2019-02-27T00:55:22.140729",
    :orthography
    "a, e, i, o, u, v, á, é, í, ó, ú, v́, à, è, ì, ò, ù, v̀, ?, ?, ?, ?, ?, ?, aa, ee, ii, oo, uu, vv, áa, ée, íi, óo, úu, v́v, aá, eé, ií, oó, uú, vv́, áà, éè, íì, óò, úù, v́v̀, aà, eè, iì, oò, uù, vv̀, áá, éé, íí, óó, úú, v́v́, V, d, t, g, k, gw, kw, j, ch, dl, tl, s, h, ʔ, l, y, w, m"}},
  :ppp-forms
  {164
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[164 "ITER2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:45.146563",
    :comments
    "H3 specification: +H3. tonicity: +tonic. TAOC: 27-29, 182. TAOC tag: ITER. Compare CRG (295) morpheme /vv- / glossed 'ITR2' and named 'Iterative 2 Prepronominal Prefix'. Compare BMA 2008 (335-336) morpheme /vv-/ glossed 'ITR2' and named 'Iterative 2 Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "ITER2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "vv|ITER2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[164 "vv" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "vv",
    :id 164,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:30.155552",
    :UUID "7f12d2e2-2efa-4716-b400-af4db53e0914",
    :speaker nil,
    :transcription "vv",
    :elicitor nil,
    :translations
    [{:id 195,
      :transcription "Iterative 2 Prepronominal Prefix",
      :grammaticality ""}]},
   153
   {:date_elicited nil,
    :tags
    [{:id 411, :name "ppp-elsewhere"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[153 "DIST1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.874704",
    :comments
    "H3 specification: +H3. TAOC: 27-29, 182. TAOC tag: DIST(i). Compare CRG (109-113, 140, 304-305) morpheme /dee- / doo-/ glossed 'DST' and named 'Distributive Prepronominal Prefix'. Compare BMA 2008 (320-325) morpheme /tee- / too-/ glossed 'DST' and named 'Distributive Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "DIST1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "too|DIST1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[153 "too" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "too",
    :id 153,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.915972",
    :UUID "65cab4dd-e1ba-4eeb-bb73-0a4eb8d67f8c",
    :speaker nil,
    :transcription "too",
    :elicitor nil,
    :translations
    [{:id 184,
      :transcription "Distributive 1 Prepronominal Prefix",
      :grammaticality ""}]},
   154
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[154 "DIST2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.975016",
    :comments
    "H3 specification: +H3. TAOC: 27-29, 182. TAOC tag: DIST(ii). Compare CRG (110-112, 123-125, 355-356) morpheme /di- / glossed 'DST2' and named 'Distributive 2 Prepronominal Prefix'. Compare BMA 2008 (320-325) morpheme /ti- / ta-/ glossed 'DST2' and named 'Distributive 2 Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "DIST2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ti|DIST2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[154 "ti" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ti",
    :id 154,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.023775",
    :UUID "0b8ffd5e-a4c7-469c-bb08-124822ae0baa",
    :speaker nil,
    :transcription "ti",
    :elicitor nil,
    :translations
    [{:id 185,
      :transcription "Distributive 2 Prepronominal Prefix",
      :grammaticality ""}]},
   165
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[165 "ITER2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:46.316819",
    :comments
    "H3 specification: +H3. tonicity: +tonic. TAOC: 27-29, 182. TAOC tag: ITER. Compare CRG (295) morpheme /vv- / glossed 'ITR2' and named 'Iterative 2 Prepronominal Prefix'. Compare BMA 2008 (335-336) morpheme /vv-/ glossed 'ITR2' and named 'Iterative 2 Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "ITER2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "vʔ|ITER2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[165 "vʔ" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "vʔ",
    :id 165,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:30.253576",
    :UUID "9f73d41b-2739-4c39-8b77-6a85c91d8c30",
    :speaker nil,
    :transcription "vʔ",
    :elicitor nil,
    :translations
    [{:id 196,
      :transcription "Iterative 2 Prepronominal Prefix",
      :grammaticality ""}]},
   149
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[149 "PART2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.345225",
    :comments
    "TAOC: 27-29, 182. TAOC tag: PART. Compare CRG (287-288) morpheme /-ii- / -iy-/ glossed 'NI2' and named 'ni- 2 Prepronominal Prefix '. Compare BMA 2008 (318) morpheme /ii-/ glossed 'PRT2' and named 'Partitive 2 Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "PART2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ii|PART2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[149 "ii" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ii",
    :id 149,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.424980",
    :UUID "bf03b721-54db-49a5-87f7-f67deda32dac",
    :speaker nil,
    :transcription "ii",
    :elicitor nil,
    :translations
    [{:id 180,
      :transcription "Partitive 2 Prepronominal Prefix",
      :grammaticality ""}]},
   157
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[157 "CISL1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.288808",
    :comments
    "H3 specification: +H3. tonicity: ±tonic. TAOC: 27-29, 182. TAOC tag: CISL. Compare CRG (288-291) morpheme /di-/ glossed 'TOW' and named 'Toward Prepronominal Prefix'. Compare CRG (292-293) morpheme /da- / di- / glossed 'MOT2' and named 'Motion Toward 2 Prepronominal Prefix'. Compare BMA 2008 (326-328) morpheme /ti- / ta-/ glossed 'CIS' and named 'Cislocative Prepronominal Prefix '.",
    :elicitation_method nil,
    :morpheme_gloss "CISL1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ti|CISL1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[157 "ti" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ti",
    :id 157,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.342756",
    :UUID "0f522bb3-6642-46f1-b2bb-56f90bb970f5",
    :speaker nil,
    :transcription "ti",
    :elicitor nil,
    :translations
    [{:id 188,
      :transcription "Cislocative 1 Prepronominal Prefix",
      :grammaticality ""}]},
   144
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[144 "IRR" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:42.676086",
    :comments
    "H3 specification: +H3. tonicity: -tonic. TAOC: 27-29, 182. TAOC tag: IRR. Compare CRG (106-109) morpheme /yi- / glossed 'IRR' and named 'Irrealis Prepronominal Prefix'. Compare BMA 2008 (297-303) morpheme /yi-/ glossed 'IRR' and named 'Irrealis Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "IRR",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "y|IRR|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[144 "y" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "y",
    :id 144,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:27.885028",
    :UUID "ae44d1af-74c7-4dbc-ac16-7aedef5d8539",
    :speaker nil,
    :transcription "y",
    :elicitor nil,
    :translations
    [{:id 175,
      :transcription "Irrealis Prepronominal Prefix",
      :grammaticality ""}]},
   159
   {:date_elicited nil,
    :tags
    [{:id 411, :name "ppp-elsewhere"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[159 "CISL1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.536811",
    :comments
    "H3 specification: +H3. tonicity: ±tonic. TAOC: 27-29, 182. TAOC tag: CISL. Compare CRG (288-291) morpheme /di-/ glossed 'TOW' and named 'Toward Prepronominal Prefix'. Compare CRG (292-293) morpheme /da- / di- / glossed 'MOT2' and named 'Motion Toward 2 Prepronominal Prefix'. Compare BMA 2008 (326-328) morpheme /ti- / ta-/ glossed 'CIS' and named 'Cislocative Prepronominal Prefix '.",
    :elicitation_method nil,
    :morpheme_gloss "CISL1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "c|CISL1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[159 "c" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "c",
    :id 159,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.569262",
    :UUID "48ffab91-ffbe-460c-b45f-e0b48f09341a",
    :speaker nil,
    :transcription "c",
    :elicitor nil,
    :translations
    [{:id 190,
      :transcription "Cislocative 1 Prepronominal Prefix",
      :grammaticality ""}]},
   156
   {:date_elicited nil,
    :tags
    [{:id 411, :name "ppp-elsewhere"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[156 "DIST2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.189706",
    :comments
    "H3 specification: +H3. TAOC: 27-29, 182. TAOC tag: DIST(ii). Compare CRG (110-112, 123-125, 355-356) morpheme /di- / glossed 'DST2' and named 'Distributive 2 Prepronominal Prefix'. Compare BMA 2008 (320-325) morpheme /ti- / ta-/ glossed 'DST2' and named 'Distributive 2 Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "DIST2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "c|DIST2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[156 "c" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "c",
    :id 156,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.232999",
    :UUID "4b3e0185-0b51-4162-a973-2901b41a8b99",
    :speaker nil,
    :transcription "c",
    :elicitor nil,
    :translations
    [{:id 187,
      :transcription "Distributive 2 Prepronominal Prefix",
      :grammaticality ""}]},
   168
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[168 "ANS" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:46.648032",
    :comments
    "Compare CRG (117-118) morpheme /gaa- / glossed 'ANS' and named 'Animate Nonsingular Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "ANS",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "kaa|ANS|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[168 "kaa" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "kaa",
    :id 168,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:30.539592",
    :UUID "18f1220c-7c4d-47ba-88de-9f84279e4bd4",
    :speaker nil,
    :transcription "kaa",
    :elicitor nil,
    :translations
    [{:id 199,
      :transcription "Animate Nonsingular Prepronominal Prefix",
      :grammaticality ""}]},
   143
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[143 "IRR" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:42.520193",
    :comments
    "H3 specification: +H3. tonicity: -tonic. TAOC: 27-29, 182. TAOC tag: IRR. Compare CRG (106-109) morpheme /yi- / glossed 'IRR' and named 'Irrealis Prepronominal Prefix'. Compare BMA 2008 (297-303) morpheme /yi-/ glossed 'IRR' and named 'Irrealis Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "IRR",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "yi|IRR|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[143 "yi" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "yi",
    :id 143,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:27.783476",
    :UUID "a263aa17-bb9d-455f-9c76-9f377f138e4a",
    :speaker nil,
    :transcription "yi",
    :elicitor nil,
    :translations
    [{:id 174,
      :transcription "Irrealis Prepronominal Prefix",
      :grammaticality ""}]},
   167
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[167 "REL" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:46.534810",
    :comments
    "H3 specification: -H3. tonicity: +tonic. TAOC: 27-29, 182. TAOC tag: REL. Compare CRG (279-282) morpheme /ji- / glossed 'REL' and named 'Relativizer Prepronominal Prefix'. Compare BMA 2008 (304-305) morpheme /ji-/ glossed 'REL' and named 'Relativizer Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "REL",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "c|REL|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[167 "c" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "c",
    :id 167,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:30.441872",
    :UUID "c741b8e6-9b6f-4ffe-9ddf-92b38610aed0",
    :speaker nil,
    :transcription "c",
    :elicitor nil,
    :translations
    [{:id 198,
      :transcription "Relative Prepronominal Prefix",
      :grammaticality ""}]},
   150
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[150 "PART2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.470341",
    :comments
    "TAOC: 27-29, 182. TAOC tag: PART. Compare CRG (287-288) morpheme /-ii- / -iy-/ glossed 'NI2' and named 'ni- 2 Prepronominal Prefix '. Compare BMA 2008 (318) morpheme /ii-/ glossed 'PRT2' and named 'Partitive 2 Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "PART2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "iy|PART2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[150 "iy" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "iy",
    :id 150,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.552187",
    :UUID "3075ca16-a8cd-4cb8-80a0-9afad9e6d939",
    :speaker nil,
    :transcription "iy",
    :elicitor nil,
    :translations
    [{:id 181,
      :transcription "Partitive 2 Prepronominal Prefix",
      :grammaticality ""}]},
   162
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[162 "ITER1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.912835",
    :comments
    "H3 specification: +H3. tonicity: +tonic. TAOC: 27-29, 182. TAOC tag: ITER. Compare CRG (293-296) morpheme /ii- /  hii- / glossed 'ITR' and named 'Iterative Prepronominal Prefix'. Compare BMA 2008 (333-336) morpheme /ii- / hii-/ glossed 'ITR' and named 'Iterative Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "ITER1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ii|ITER1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[162 "ii" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ii",
    :id 162,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.885458",
    :UUID "43bc4e3b-58b7-4015-a3ce-06ab37c4bf2d",
    :speaker nil,
    :transcription "ii",
    :elicitor nil,
    :translations
    [{:id 193,
      :transcription "Iterative 1 Prepronominal Prefix",
      :grammaticality ""}]},
   151
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[151 "DIST1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.602758",
    :comments
    "H3 specification: +H3. TAOC: 27-29, 182. TAOC tag: DIST(i). Compare CRG (109-113, 140, 304-305) morpheme /dee- / doo-/ glossed 'DST' and named 'Distributive Prepronominal Prefix'. Compare BMA 2008 (320-325) morpheme /tee- / too-/ glossed 'DST' and named 'Distributive Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "DIST1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "tee|DIST1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[151 "tee" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "tee",
    :id 151,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.660095",
    :UUID "e410e214-ab0d-439d-bc9f-3be411b09c78",
    :speaker nil,
    :transcription "tee",
    :elicitor nil,
    :translations
    [{:id 182,
      :transcription "Distributive 1 Prepronominal Prefix",
      :grammaticality ""}]},
   155
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[155 "DIST2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.082012",
    :comments
    "H3 specification: +H3. TAOC: 27-29, 182. TAOC tag: DIST(ii). Compare CRG (110-112, 123-125, 355-356) morpheme /di- / glossed 'DST2' and named 'Distributive 2 Prepronominal Prefix'. Compare BMA 2008 (320-325) morpheme /ti- / ta-/ glossed 'DST2' and named 'Distributive 2 Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "DIST2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "t|DIST2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[155 "t" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "t",
    :id 155,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.137653",
    :UUID "6a118d7e-b5fe-45e9-aa24-37d5705ec7c2",
    :speaker nil,
    :transcription "t",
    :elicitor nil,
    :translations
    [{:id 186,
      :transcription "Distributive 2 Prepronominal Prefix",
      :grammaticality ""}]},
   166
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[166 "REL" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:46.426252",
    :comments
    "H3 specification: -H3. tonicity: +tonic. TAOC: 27-29, 182. TAOC tag: REL. Compare CRG (279-282) morpheme /ji- / glossed 'REL' and named 'Relativizer Prepronominal Prefix'. Compare BMA 2008 (304-305) morpheme /ji-/ glossed 'REL' and named 'Relativizer Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "REL",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ci|REL|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[166 "ci" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ci",
    :id 166,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:30.334817",
    :UUID "f95967c1-a181-44ad-a31f-9d4f57533711",
    :speaker nil,
    :transcription "ci",
    :elicitor nil,
    :translations
    [{:id 197,
      :transcription "Relative Prepronominal Prefix",
      :grammaticality ""}]},
   146
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[146 "TRNSL" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:42.919540",
    :comments
    "H3 specification: +H3. tonicity: -tonic. TAOC: 27-29, 182. TAOC tag: TRNSL. Compare CRG (104-106) morpheme /wi- / glossed 'TRN' and named 'Translocative Prepronominal Prefix'. Compare BMA 2008 (307-312) morpheme /wi-/ glossed 'TRN' and named 'Translocative Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "TRNSL",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "w|TRNSL|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[146 "w" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "w",
    :id 146,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.115942",
    :UUID "c005c838-3b2a-4827-b4f4-024e9afc0f29",
    :speaker nil,
    :transcription "w",
    :elicitor nil,
    :translations
    [{:id 177,
      :transcription "Translocative Prepronominal Prefix",
      :grammaticality ""}]},
   148
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[148 "PART1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.189508",
    :comments
    "H3 specification: ±H3. tonicity: ±tonic. TAOC: 27-29, 182. TAOC tag: PART. Compare CRG (283-288) morpheme /ni- / glossed 'NI' and named 'ni- Prepronominal Prefix '. Compare BMA 2008 (312-317) morpheme /ni-/ glossed 'PRT' and named 'Partitive Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "PART1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "n|PART1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[148 "n" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "n",
    :id 148,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.310437",
    :UUID "33722ced-ba80-442e-9b7c-7eb0388b06df",
    :speaker nil,
    :transcription "n",
    :elicitor nil,
    :translations
    [{:id 179,
      :transcription "Partitive 1 Prepronominal Prefix",
      :grammaticality ""}]},
   152
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[152 "DIST1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.754273",
    :comments
    "H3 specification: +H3. TAOC: 27-29, 182. TAOC tag: DIST(i). Compare CRG (109-113, 140, 304-305) morpheme /dee- / doo-/ glossed 'DST' and named 'Distributive Prepronominal Prefix'. Compare BMA 2008 (320-325) morpheme /tee- / too-/ glossed 'DST' and named 'Distributive Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "DIST1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "t|DIST1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[152 "t" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "t",
    :id 152,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.795405",
    :UUID "93a15e20-0d2a-4525-861a-daee9f0a656f",
    :speaker nil,
    :transcription "t",
    :elicitor nil,
    :translations
    [{:id 183,
      :transcription "Distributive 1 Prepronominal Prefix",
      :grammaticality ""}]},
   158
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[158 "CISL1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.423715",
    :comments
    "H3 specification: +H3. tonicity: ±tonic. TAOC: 27-29, 182. TAOC tag: CISL. Compare CRG (288-291) morpheme /di-/ glossed 'TOW' and named 'Toward Prepronominal Prefix'. Compare CRG (292-293) morpheme /da- / di- / glossed 'MOT2' and named 'Motion Toward 2 Prepronominal Prefix'. Compare BMA 2008 (326-328) morpheme /ti- / ta-/ glossed 'CIS' and named 'Cislocative Prepronominal Prefix '.",
    :elicitation_method nil,
    :morpheme_gloss "CISL1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "t|CISL1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[158 "t" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "t",
    :id 158,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.451149",
    :UUID "c9e07a9f-2441-4f67-a7e5-c1a475c49e18",
    :speaker nil,
    :transcription "t",
    :elicitor nil,
    :translations
    [{:id 189,
      :transcription "Cislocative 1 Prepronominal Prefix",
      :grammaticality ""}]},
   145
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[145 "TRNSL" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:42.811633",
    :comments
    "H3 specification: +H3. tonicity: -tonic. TAOC: 27-29, 182. TAOC tag: TRNSL. Compare CRG (104-106) morpheme /wi- / glossed 'TRN' and named 'Translocative Prepronominal Prefix'. Compare BMA 2008 (307-312) morpheme /wi-/ glossed 'TRN' and named 'Translocative Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "TRNSL",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "wi|TRNSL|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[145 "wi" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "wi",
    :id 145,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:27.996691",
    :UUID "d1c4c8b0-570e-422f-b716-4831cee6cef2",
    :speaker nil,
    :transcription "wi",
    :elicitor nil,
    :translations
    [{:id 176,
      :transcription "Translocative Prepronominal Prefix",
      :grammaticality ""}]},
   163
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[163 "ITER1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:45.016457",
    :comments
    "H3 specification: +H3. tonicity: +tonic. TAOC: 27-29, 182. TAOC tag: ITER. Compare CRG (293-296) morpheme /ii- /  hii- / glossed 'ITR' and named 'Iterative Prepronominal Prefix'. Compare BMA 2008 (333-336) morpheme /ii- / hii-/ glossed 'ITR' and named 'Iterative Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "ITER1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "iʔ|ITER1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[163 "iʔ" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "iʔ",
    :id 163,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:30.032811",
    :UUID "74b9f4ef-7c4f-45b8-8194-c7e40d661872",
    :speaker nil,
    :transcription "iʔ",
    :elicitor nil,
    :translations
    [{:id 194,
      :transcription "Iterative 1 Prepronominal Prefix",
      :grammaticality ""}]},
   169
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[169 "ANS" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:46.752337",
    :comments
    "Compare CRG (117-118) morpheme /gaa- / glossed 'ANS' and named 'Animate Nonsingular Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "ANS",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ka|ANS|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[169 "ka" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ka",
    :id 169,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:30.656795",
    :UUID "e9435f73-4ac8-4820-b37b-41003f1aceaa",
    :speaker nil,
    :transcription "ka",
    :elicitor nil,
    :translations
    [{:id 200,
      :transcription "Animate Nonsingular Prepronominal Prefix",
      :grammaticality ""}]},
   160
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[160 "CISL2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.665222",
    :comments
    "H3 specification: +H3. tonicity: ±tonic. TAOC: 27-29, 182. TAOC tag: CISL. Compare CRG (289-290) morpheme /da-/ glossed 'TOW2' and named 'Toward 2 Prepronominal Prefix'. Compare CRG (291-293) morpheme /da- / glossed 'MOT' and named 'Motion Toward Prepronominal Prefix'. Compare CRG (115-117) morpheme /da- / glossed 'CMF' and named 'Completive Future Prepronominal Prefix '. Compare BMA 2008 (328-329) morpheme /ta-/ glossed 'CSM' and named 'Cislocative Motion Prepronominal Prefix '. Compare BMA 2008 (329-332) morpheme /ta-/ glossed 'FUT' and named 'Future ta- Prepronominal Prefix '.",
    :elicitation_method nil,
    :morpheme_gloss "CISL2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ta|CISL2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[160 "ta" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ta",
    :id 160,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.669131",
    :UUID "d053efdd-4671-4d73-8f0f-b1c6d7b1fa1c",
    :speaker nil,
    :transcription "ta",
    :elicitor nil,
    :translations
    [{:id 191,
      :transcription "Cislocative 2 Prepronominal Prefix",
      :grammaticality ""}]},
   147
   {:date_elicited nil,
    :tags
    [{:id 410, :name "ppp-pre-consonantal"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[147 "PART1" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:43.031433",
    :comments
    "H3 specification: ±H3. tonicity: ±tonic. TAOC: 27-29, 182. TAOC tag: PART. Compare CRG (283-288) morpheme /ni- / glossed 'NI' and named 'ni- Prepronominal Prefix '. Compare BMA 2008 (312-317) morpheme /ni-/ glossed 'PRT' and named 'Partitive Prepronominal Prefix'.",
    :elicitation_method nil,
    :morpheme_gloss "PART1",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "ni|PART1|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[147 "ni" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "ni",
    :id 147,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:28.225611",
    :UUID "861dd8ac-0b00-4a75-90f9-c1d98cafb696",
    :speaker nil,
    :transcription "ni",
    :elicitor nil,
    :translations
    [{:id 178,
      :transcription "Partitive 1 Prepronominal Prefix",
      :grammaticality ""}]},
   161
   {:date_elicited nil,
    :tags
    [{:id 409, :name "ppp-pre-vocalic"}
     {:id 422, :name "ingest-uchihara-root:2019-02-27T02:27:36.768Z"}],
    :morpheme_break_ids [[[[161 "CISL2" "PPP"]]]],
    :verifier nil,
    :modifier
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :narrow_phonetic_transcription "",
    :syntactic_category_string "PPP",
    :semantics "",
    :enterer
    {:id 1, :first_name "Admin", :last_name "Admin", :role "administrator"},
    :datetime_modified "2019-02-27T02:27:44.800533",
    :comments
    "H3 specification: +H3. tonicity: ±tonic. TAOC: 27-29, 182. TAOC tag: CISL. Compare CRG (289-290) morpheme /da-/ glossed 'TOW2' and named 'Toward 2 Prepronominal Prefix'. Compare CRG (291-293) morpheme /da- / glossed 'MOT' and named 'Motion Toward Prepronominal Prefix'. Compare CRG (115-117) morpheme /da- / glossed 'CMF' and named 'Completive Future Prepronominal Prefix '. Compare BMA 2008 (328-329) morpheme /ta-/ glossed 'CSM' and named 'Cislocative Motion Prepronominal Prefix '. Compare BMA 2008 (329-332) morpheme /ta-/ glossed 'FUT' and named 'Future ta- Prepronominal Prefix '.",
    :elicitation_method nil,
    :morpheme_gloss "CISL2",
    :source nil,
    :syntactic_category {:id 82, :name "PPP"},
    :break_gloss_category "tay|CISL2|PPP",
    :grammaticality "",
    :speaker_comments "",
    :morpheme_gloss_ids [[[[161 "tay" "PPP"]]]],
    :status "tested",
    :syntax "",
    :morpheme_break "tay",
    :id 161,
    :phonetic_transcription "",
    :files [],
    :datetime_entered "2019-02-27T00:55:29.769091",
    :UUID "cf6e2a3a-5a28-41c5-bf79-a59ad88bbb43",
    :speaker nil,
    :transcription "tay",
    :elicitor nil,
    :translations
    [{:id 192,
      :transcription "Cislocative 2 Prepronominal Prefix",
      :grammaticality ""}]}}})


(def todos (atom []))

(defn add-todo!
  [task]
  (swap! todos conj task))

(defn get-todo!
  []
  (let [[old new] (swap-vals! todos pop)]
    (peek old)))

(comment

  (fetch-mod-sfxs)

  (fetch-upload-mod-sfx-forms fake-state :disable-cache false)

  (->
   (fetch-upload-mod-sfx-forms fake-state :disable-cache false)
   first
   :mod-sfx-form-maps)

  (select-keys {:a 1 :b 2 :c 3} '(:a :c))

  (conj {:a 1 :b 2} {:c 3})

  (merge {:a 1 :b 2} {:c 3})

  (= (conj {:a 1 :b 2} {:c 3}) (merge {:a 1 :b 2} {:c 3}))

  (= (conj {:a 1 :b 2} {:a 44 :c 3}) (merge {:a 1 :b 2} {:a 44 :c 3}))

  (conj {:a 1} [:b 2])

  (conj {:a 1} [:b 2] [:c 3] [:z 999])

  (apply conj '({} [:a 1] [:b 2]))

  (seq {:a 1 :b 2})

  (sorted-map 1 1 2 3)

  (rand-int 10)

  (map #(do [% (rand-int 10)]) (range 10))

  (flatten (map #(do [% (rand-int 10)]) (range 10)))

  (apply sorted-map (flatten (map #(do [% (rand-int 10)]) (range 10))))

  (:a {:a 2 :b 3} 4)

  (:c {:a 2 :b 3} 4)

  (assoc {:a 2} :a 3)

  (assoc {:a 2} :b 3)

  (dissoc {:a 2} :a)

  (dissoc {:a 2} :b)

  (dissoc {:a 1 :b 2 :c 3} :a 2 :b)

  (rand)

  (rand-int 100)

  ((fnil inc 0) nil)

  ((fnil inc 0) 1)

  ((fnil inc 0) 0)

  (contains? [:a :b] 0)

  (contains? [:a :b] 1)

  (contains? [:a :b] 2)

  (contains? #{:a :b} 2)
 
  (contains? #{:a :b} :a)

  (contains? {:a 1 :b 2} :a)

  (contains? {:a 1 :b 2} 2)

  (contains? {:a 1 :b 2} 0)

  (let [q (conj (clojure.lang.PersistentQueue/EMPTY) 1 3 5 7)]
    q)

  (let [q (conj (clojure.lang.PersistentQueue/EMPTY) 1 3 5 7)]
    (vec (pop q)))

  (pop [1 2 3])

  (pop '(1 2 3))

  (peek [1 2 3])

  (peek '(1 2 3))
  
 

)
