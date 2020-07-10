(defproject net.ofnir/graphviz-dsl "0.1.0-SNAPSHOT"
  :description "Generate GraphViz DOT files from EDN with styling similar to CSS"
  :url "https://github.com/christoph-frick/graphviz-dsl"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/test.check "1.0.0"]
                 [com.stuartsierra/dependency "1.0.0"]
                 [expound "0.8.5"]
                 [fmnoise/flow "4.0.0"]]
  :main ^:skip-aot net.ofnir.graphviz-dsl.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
