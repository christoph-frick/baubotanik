(ns net.ofnir.baubotanik.macro-test
  (:require
   [clojure.test :refer [deftest is are]]
   [net.ofnir.baubotanik.macro :as sut]))

(deftest test-macro-arg?
  (are [arg result] (= (sut/macro-arg? arg) result)
    '%1 true
    'lol false))

(deftest test-expand-macro-positional
  (are [result macro in out] (= result (sut/expand-macro-positional macro in out))
    '[:one] '(m %1) '(m :one) '[%1]
    '[1 2 3 %4] '(m %1 %2 %3) '(m 1 2 3) '[%1 %2 %3 %4]
    '[:n1 :n2 {:styles [:test]}] '(m %1 %2) '(m :n1 :n2) '[%1 %2 {:styles [:test]}]))

(deftest test-expand-macro-rest
  (are [result macro in out] (= result (sut/expand-macro-rest macro in out))
    '[:x 1 2 3] '(m %&) '(m 1 2 3) '[:x %&]))

(deftest test-macro-expand-all
  ; TODO: splice macro result
  (is
   (=
    '(digraph
      [:node1 {:label [:table {:border 0}
                       [:tr [:td {:align :left} [:font {:face "Monospace"} "contNavActn = event.postpone()"]]]
                       [:tr [:td {:align :left} [:font [:b "Don't"] " navigate away yet"]]]
                       [:tr [:td {:align :left} "Provides callback to still continue"]]]}]
      [:node2]
      [:node1 :node2 {:styles [:link]}]
      #_[:node1 :node1]
      #_[:node1 :node2])

    (sut/macro-expand-all
     '{(table %&) [:table {:border 0} %&]
       (trl %&) [:tr [:td {:align :left} %&]]
       (monospace %&) [:font {:face "Monospace"} %&]
       (link %1 %2) [%1 %2 {:styles [:link]}]
       #_#_(@link-and-loop %1 %2) [[%1 %1] [%1 %2]]}
     '(digraph
       [:node1 {:label (table
                        (trl (monospace "contNavActn = event.postpone()"))
                        (trl [:font [:b "Don't"] " navigate away yet"])
                        (trl "Provides callback to still continue"))}]
       [:node2]
       (link :node1 :node2)
       #_(@link-and-loop :node1 :node2))))))
