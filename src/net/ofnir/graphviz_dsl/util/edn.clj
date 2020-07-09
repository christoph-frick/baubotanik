(ns net.ofnir.graphviz-dsl.util.edn
  (:require [clojure.edn :as edn]
            [net.ofnir.graphviz-dsl.util.slurp :as s]))

(defn slurp-file
  [name]
  (-> name 
      (s/slurp-file)
      (edn/read-string)))

(defn slurp-resource
  [name]
  (-> name 
      (s/slurp-resource)
      (edn/read-string)))
