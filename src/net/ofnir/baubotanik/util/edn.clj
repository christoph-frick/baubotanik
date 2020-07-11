(ns net.ofnir.baubotanik.util.edn
  (:require [clojure.edn :as edn]
            [net.ofnir.baubotanik.util.slurp :as s]))

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
