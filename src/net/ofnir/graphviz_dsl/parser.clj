(ns net.ofnir.graphviz-dsl.parser
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]))

(s/def ::style map?)

(s/def ::maybe-style (s/? ::style))

(s/def ::name keyword?)

(s/def ::node (s/cat :node-id ::name 
                     :style ::maybe-style))

(s/def ::edge (s/cat :source-node-id ::name 
                     :target-node-id ::name 
                     :style ::maybe-style))

(s/def ::style-block (s/cat :type #{'node 'edge} 
                            :style ::style))

(s/def ::inner-block (s/alt :block ::named-block
                            :style ::style-block
                            :node ::node
                            :edge ::edge))

(s/def ::children (s/+ (s/spec ::inner-block)))

(s/def ::named-block (s/cat :type #{'subgraph}
                            :name ::name
                            :style ::maybe-style
                            :children ::children))

(s/def ::graph (s/cat :type #{'digraph}
                      :style ::maybe-style
                      :children ::children))

(s/def ::root (s/keys 
                 :req-un [::graph]
                 :opt-un  [::styles]))

(defn safe-conform
  [spec data]
  (if (s/valid? spec data)
    (s/conform spec data)
    (throw (ex-info (expound/expound-str spec data) {}))))

(defn parse
  [graph]
  (safe-conform ::root graph))
