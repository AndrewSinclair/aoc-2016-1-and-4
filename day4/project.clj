(defproject day4 "0.1.0-SNAPSHOT"
  :description "advent of code 2016 day 4"
  :url "https://andrewsinclair.github.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/test.check "0.9.0"]
                 [com.jakemccrary/lein-test-refresh "0.20.0"]]
  :main ^:skip-aot day4.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-auto "0.1.2"]])
