(defproject net.ofnir/baubotanik "0.1.0-SNAPSHOT"
  :description "Generate GraphViz DOT files from EDN with styling similar to CSS"
  :url "https://github.com/christoph-frick/baubotanik"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.2-alpha1"]
                 ; [org.clojure/test.check "1.0.0"]
                 [com.stuartsierra/dependency "1.0.0"]
                 [expound "0.8.5"]
                 [fmnoise/flow "4.0.0"]]
  :main net.ofnir.baubotanik.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :native-image {:jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                                 "-Dclojure.spec.skip-macros=true"]}}
             :test {:native-image {:opts ["--report-unsupported-elements-at-runtime"
                                          "--initialize-at-build-time"
                                          "--verbose"]}}}
  :plugins [[io.taylorwood/lein-native-image "0.3.1"]]
  :native-image {:name "baubotanik"
                 :opts ["--verbose"
                        "--no-fallback"
                        "--report-unsupported-elements-at-runtime"
                        "--initialize-at-build-time"]})
