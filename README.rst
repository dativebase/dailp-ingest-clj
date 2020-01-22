================================================================================
  DAILP Ingest
================================================================================

DAILP Ingest is a tool that reads Cherokee data from a set of spreadsheets and
makes HTTP calls to upload those data to an Online Linguistic Database (OLD)
instance.


Installation
================================================================================

Clone the source from GitHub::

    $ git clone https://github.com/dativebase/dailp-ingest-clj.git

Using Leiningen, create the standalone .jar file::

    $ lein uberjar

**Note: you must have Leiningen and Clojure installed.**


Usage
================================================================================

To ingest the data from the DAILP Google Sheet sources to a specific OLD
instance, supply a valid OLD instance's URL, username and password::

    $ java -jar target/uberjar/dailp-ingest-clj-0.1.0-SNAPSHOT-standalone.jar \
      https://some.domain.com/path/to/old/instance/ \
      someusername \
      somepassword

For example, if you are running a local OLD instance using the DativeBase Docker
Compose deployment method, then the following will probably work::

    $ java -jar target/uberjar/dailp-ingest-clj-0.1.0-SNAPSHOT-standalone.jar \
      http://127.0.0.1:61001/old/ \
      admin \
      adminA_1

Alternatively, use lein run::

    $ lein run \
      https://some.domain.com/path/to/old/instance/ \
      someusername \
      somepassword

**Note: this ingest script requires that you have read access to the Google
Sheet files listed below.**


Ingestion Plan
================================================================================

All URLs for the Google Sheets referenced below can be obtaibed by suffixing the supplied sheet identifiers to::

    https://docs.google.com/spreadsheets/d/
1. Functional morphemes:

   a. Tags (DONE): 1eEk3JP2WTkP8BBShBHURrripKPredy-sCutQMiGfVmo
   b. Orthographic Inventories (DONE): 16Dfq04tCSP0kuqBdMX1R3DHJ7kB6RJ3Uy7-ufN-Y_w8
   c. Syntactic Categories (DONE): 159i_Cygdqsnp55QBzqJu7eozxsNEiVIiXhwEzls3q7g
   d. Prepronominal Prefixes (DONE): 12v5fqtOztwwLeEaKQJGMfziwlxP4n60riMsN9dYw9Xc
   e. Pronominal Prefixes (DONE):

      - Combined (DONE): 1OMzkbDGY1BqPR_ZwJRe4-F5_I12Ao5OJqqMp8Ej_ZhE
      - Sets A & B (DONE): 1D0JZEwE-dj-fKppbosaGhT7Xyyy4lVxmgG02tpEi8nw
      - Reflexive & Middle (DONE): 1Q_q_1MZbmZ-g0bmj1sQouFFDnLBINGT3fzthPgqgkqo

   f. Modal Suffixes (DONE): 1QWYWFeK6xy7zciIliizeW2hBfuRPNk6dK5rGJf2pdNc
   g. Aspectual Suffixes (DONE): 19jPHtphsvWDliWq9z3WL_Fz6omHCFTFseD6fh1FLY70

2. Verbs (roots and inflected forms) (1/2 DONE or ALL DONE?):

   a. DF1975 (DONE):
   b. DF2003 (DONE):


Useful links
--------------------------------------------------------------------------------

- Google Drive folder with master data sets:
  https://drive.google.com/drive/folders/1U2ZtSQfMbX1b86SbX3BJw0Cx5Fs78vZ7

- Google doc describing the functional closed-class spreadsheets:
  https://docs.google.com/document/d/1jUgIjOMH_c0HHnQaJZjBXny7XPConrphToaz8nyGDX0/edit#heading=h.j48zjb5g20tm


Questions
================================================================================

DF1975--Master
--------------------------------------------------------------------------------

1. Syntactic categories for Verb roots and inflected forms.

   a. Should all of the verb roots have category "V"? Or should transitivity
      information be used to determine a transitivity-based category, e.g.,
      "VT", "VTA", etc.?

   b. What syntactic category do we want the inflected verb forms to have? I
      have been giving them "S". We could give them "VP" or some such thing ...

2. Sources. What should the Sources be for DF 1975 and DF 2003? The best thing
   would be for me to create a Google Sheet for DAILP sources and automate the
   ingest of it. The ingest script will then have to be modified to document
   the correct source for each form ingested.

   - See my draft sources GSheet at:
     https://docs.google.com/spreadsheets/d/1W46XymhtohAizs_KVRCNvfTUL0k4-LWwYbEv8aHau_4/edit?usp=sharing

3. Do the "surface form" values of the DF1975--Master spreadsheet need to be
   modified in any way?

4. DF1975-Master Questions.

   a. Row 1881 has a root line that only has values for "Transitivity" "I" and
      "UDB Class" "4a.i.irr.". I have been taking this to mean that there is a
      verb root with shape "hno:" that is the intransitive counterpart of
      transitive "tell". I have been adding a new OLD form for this intransitive
      verb root. Is this correct?

      - Note: There appear to be 4 rows with only a "Transitivity" value as
        described just above, 3 with "I", and one with "T".

   b. There are about 100 forms lacking translations. To find them, search in
      the OLD for translations with the following transcription value: "FIXME
      TRANSLATION NEEDED".

   c. There are a handful of forms lacking valid morpheme gloss values. In some
      (9) cases a default value of "FIXME.MORPHEME.GLOSS.NEEDED" was used.
      Search for this value to find them. In a handful of cases, a value was
      constructed using the first translation value. Here are those constructed
      values:

      - "scatter.(intransitive)"
      - "pour.into.a.container,.fill.up"
      - "(sun.or.moon).shine,.be.sunny"
      - "(the.ground).become.frosty"
      - "thunder"

5. Tags for Affix Allomorphs.

   a. Allomorph 4 of "Reflexive & Middle Pronominal Prefixes". What tag should
      be used for these? I am using the "pp-pre-v" tag. Is this correct?

   b. Allomorph 4 of "Modal Suffixes". What tag should be used for these? I am
      using the "mod-pre-v" tag. Is this correct?

6. Morpheme break transcription conventions mismatch. I notice that different
   transcription conventions are being used for the morpheme break line of
   morphemes and the same line of inflected verb forms. For example, this
   (DF2003) inflected verb::

       transcription  ᎯᏕᎸᎢ
       morpheme break /hi:-t-e:!l-v:'i/
       morpheme gloss 2SG>AN-give.LG-PFT-FUT.IMP
       translations   Give it (LG) to him later!

   Presumably contains this 2SG>AN morpheme::

       transcription  hii
       morpheme break /hii/
       morpheme gloss 2SG>AN
       translations   Set A 2SG AN Pronominal Prefix

   However, observer that the colon is being used to signify length in the
   former (``hi:``) while double vowels are being used in the latter (``hii``).

   Similarly, the DF2003 morpheme break values are using the glottal stop
   Unicode character while the aspectual suffixes morpheme break values are
   using the apostrophe.

   We should probably enforce some consistency here, especially in anticipation
   of parser development. Guidance on which forms to modify?



License
================================================================================

Copyright © 2019 Joel Dunham

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
