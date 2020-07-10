(ns net.ofnir.graphviz-dsl.main
  (:gen-class)
  (:require [net.ofnir.graphviz-dsl.util.edn :as edn]
            [net.ofnir.graphviz-dsl.parser :as p]
            [net.ofnir.graphviz-dsl.writer :as w]
            [clojure.java.io :as io]
            [fmnoise.flow :as flow]))

(defn process
  [input-file-name output]
  (->> (flow/call #(edn/slurp-file input-file-name))
       (flow/then-call p/parse)
       (flow/then-call w/write)
       (flow/then-call #(with-open [w (io/writer output)]
                          (.write w %)))
       (flow/else #(println (ex-message %)))))

(defn -main
  [& [input-file-name output-file-name]]
  (when input-file-name
    (let [output (if output-file-name (io/file output-file-name) *out*)]
      (process input-file-name output))))
