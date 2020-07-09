(ns net.ofnir.graphviz-dsl.util.slurp
  (:require [clojure.java.io :as io]))

(defn slurp-file
  [file-name]
  (-> (io/file file-name) (slurp)))

(defn slurp-resource
  [file-name]
  (-> (io/resource file-name) (slurp)))
