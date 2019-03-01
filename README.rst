================================================================================
  DAILP Ingest
================================================================================

DAILP Ingest is a tool that reads Cherokee data from a set of spreadsheets and
makes HTTP calls to upload those data to an Online Linguistic Database (OLD)
instance.

Ingestion Plan
================================================================================

1. Functional morphemes:

   a. Orthographic Inventories:
      https://docs.google.com/spreadsheets/d/16Dfq04tCSP0kuqBdMX1R3DHJ7kB6RJ3Uy7-ufN-Y_w8/edit#gid=886203972
   b. Prepronominal Prefixes:
      https://docs.google.com/spreadsheets/d/12v5fqtOztwwLeEaKQJGMfziwlxP4n60riMsN9dYw9Xc/edit#gid=0
   c. Modal Suffixes:
      https://docs.google.com/spreadsheets/d/1QWYWFeK6xy7zciIliizeW2hBfuRPNk6dK5rGJf2pdNc/edit#gid=0
   d. Aspectual Suffixes:
      https://docs.google.com/spreadsheets/d/19jPHtphsvWDliWq9z3WL_Fz6omHCFTFseD6fh1FLY70/edit#gid=0

2. DF1975
3. DF2003

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

1. Should all of the verb roots have category "V"? Or should transitivity
   information be used to determine a transitivity-based category, e.g., "VT",
   "VTA", etc.?

2. What should the Sources be for DF 1975 and DF 2003? The best thing would be
   for me to create a Google Sheet for DAILP sources and automate the ingest of
   it. The ingest script will then have to be modified to document the correct
   source for each form ingested.

   - See my draft sources GSheet at:
     https://docs.google.com/spreadsheets/d/1W46XymhtohAizs_KVRCNvfTUL0k4-LWwYbEv8aHau_4/edit?usp=sharing

3. Do the "surface form" values of the DF1975--Master spreadsheet need to be
   modified in any way?

4. What syntactic category do we want the inflected verb forms to have? I have
   been giving them "S". We could give them "VP" or some such thing ...


Installation
================================================================================

FIXME


Usage
================================================================================

FIXME: explanation

    $ java -jar dailp-ingest-clj-0.1.0-standalone.jar [args]


Options
================================================================================

FIXME: listing of options this app accepts.


Examples
================================================================================

...

Bugs
--------------------------------------------------------------------------------

...


License
================================================================================

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
