(ns net.ofnir.graphviz-dsl.parser-test
  (:require [clojure.test :refer [deftest testing is are]]
            [net.ofnir.graphviz-dsl.parser :as t]
            [clojure.spec.alpha :as s]
            [net.ofnir.graphviz-dsl.util.edn :as edn]))

(defn valid?
  [spec data]
  (or (s/valid? spec data) (throw (s/explain spec data))))

(deftest test-spec
  (are [file-name]
       (valid? ::t/root (edn/slurp-resource file-name))
    "sample.edn"))

(deftest test-parser-error-handling
  (is (thrown? Exception (t/parse {}))))
