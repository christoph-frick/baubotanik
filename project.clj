(defproject net.ofnir/baubotanik "0.1.0-SNAPSHOT"
  :description "Generate GraphViz DOT files from EDN with styling similar to CSS"
  :url "https://github.com/christoph-frick/baubotanik"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/test.check "1.1.1"]
                 [com.stuartsierra/dependency "1.0.0"]
                 [expound "0.9.0"]
                 [fmnoise/flow "4.2.1"]
                 [hiccup "1.0.5"]]
  :plugins [[io.taylorwood/lein-native-image "0.3.1"]]
  :native-image {:name "baubotanik"
                 :opts ["--verbose"
                        "--initialize-at-build-time"
                        "--report-unsupported-elements-at-runtime"
                        "--no-server"
                        "--no-fallback"]}
  :main net.ofnir.baubotanik.main
  :target-path "target/%s"
  :global-vars {*warn-on-reflection* true}
  :profiles {:uberjar {:aot :all
                       :native-image {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
             :kaocha {:dependencies [[lambdaisland/kaocha "1.71.1119"]]}}
  :aliases {"kw" ["kaocha" "--watch"]
            "kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]})
