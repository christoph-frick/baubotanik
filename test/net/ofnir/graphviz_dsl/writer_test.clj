(ns net.ofnir.graphviz-dsl.writer-test
  (:require [clojure.test :refer [deftest testing is are]]
            [net.ofnir.graphviz-dsl.writer :as t]
            [net.ofnir.graphviz-dsl.parser :as p]
            [net.ofnir.graphviz-dsl.util.slurp :as r]  
            [net.ofnir.graphviz-dsl.util.edn :as edn]))

(deftest test-expand-styles
  (let [known-styles {:a {:x 1}}
        style {:styles [:a] :y 2}]
    (is (=
         (t/expand-styles known-styles style)
         {:x 1 :y 2}))))

(deftest test-build-known-styles
  (let [styles {:a {:x 1}
                :b {:styles [:c]
                    :z 3}
                :c {:styles [:a]
                    :y 2}
                :d {}}]
    (is (= {:a {:x 1}
            :b {:x 1 :y 2 :z 3}
            :c {:x 1 :y 2}
            :d {}}
           (t/build-known-styles styles)))))

(deftest test-kw-to-attr
  (is (= "a_b" (t/write-attr-key :a-b))))

(deftest test-write-attr
  (is (= "\"test\\\"test\"" (t/write-attr-value "test\"test"))))

(deftest test-write-attr-list
  (is (=
       "a=\"true\"\nb=\"42\"\nc=\"test\"\n"
       (t/write-attr-list {:a true :b 42 :c "test"}))))

(deftest test-write-graph
  (are [base-name]
       (= (r/slurp-resource (str base-name ".dot"))
          (-> (edn/slurp-resource (str base-name ".edn")) (p/parse) (t/write-graph)))
    "sample"))
