(ns net.ofnir.baubotanik.main
  (:gen-class)
  (:require [net.ofnir.baubotanik.util.edn :as edn]
            [net.ofnir.baubotanik.parser :as p]
            [net.ofnir.baubotanik.writer :as w]
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
