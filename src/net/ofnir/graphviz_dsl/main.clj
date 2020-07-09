(ns net.ofnir.graphviz-dsl.main
  (:gen-class)
  (:require [net.ofnir.graphviz-dsl.util.edn :as edn]
            [net.ofnir.graphviz-dsl.parser :as p]
            [net.ofnir.graphviz-dsl.writer :as w]))

(defn -main
  [& [file-name]]
  (when file-name
    (let [data (edn/slurp-file file-name)
          graph (p/parse data)]
      (println (w/write graph))
      (flush))))
