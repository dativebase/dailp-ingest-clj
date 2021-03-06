(defproject dailp-ingest-clj "0.1.0-SNAPSHOT"
  :description "DAILP Cherokee Ingest: from Google Sheet to an Online Linguistic
                Database instance"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/test.check "0.9.0"]
                 [clj-time "0.15.0"]
                 [inflections "0.13.2"]
                 [slingshot "0.12.2"]
                 [org.onlinelinguisticdatabase/old-client "0.1.0"]
                 [com.google.api-client/google-api-client "1.21.0"]
                 [com.google.gdata/gdata-core "1.0"]
                 [com.google.gdata/gdata-spreadsheet "3.0"]]
  :checkout-deps-shares ^:replace [:source-paths :resource-paths :compile-path
                                   #=(eval leiningen.core.classpath/checkout-deps-paths)]
  :main ^:skip-aot dailp-ingest-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
