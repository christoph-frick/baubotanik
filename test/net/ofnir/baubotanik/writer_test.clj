(ns net.ofnir.baubotanik.writer-test
  (:require [clojure.test :refer [deftest testing is are]]
            [net.ofnir.baubotanik.writer :as t]
            [net.ofnir.baubotanik.parser :as p]
            [net.ofnir.baubotanik.util.slurp :as r]
            [net.ofnir.baubotanik.util.edn :as edn]))

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
  (is (= "a_b" (t/attr-key :a-b))))

(deftest test-attr
  (is (= "\"test\\\"test\"" (t/attr-value "test\"test"))))

(deftest test-attrs
  (is (=
       "a=\"true\"\nb=\"42\"\nc=\"test\"\n"
       (t/attrs {:indent 0} {:a true :b 42 :c "test"}))))

(deftest test-graph
  (are [base-name]
       (= (r/slurp-resource (str base-name ".dot"))
          (-> (edn/slurp-resource (str base-name ".edn")) (p/parse) (t/write)))
    "sample"))
