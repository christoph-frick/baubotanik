(ns net.ofnir.baubotanik.writer
  (:require [com.stuartsierra.dependency :as dep]
            [clojure.string :as str]))

(defn expand-styles
  "Materialize a style, that depends on other, known styles"
  [known-styles style]
  (if (contains? style :styles)
    (let [styles (:styles style)
          style (dissoc style :styles)]
      (apply
       merge
       (conj
        (mapv (fn [s] (get known-styles s {})) styles)
        style)))
    style))

(defn build-known-styles
  "Build the known styles map by expanding all styles in the given map going by topological order"
  [styles]
  (let [graph (reduce
               (fn [graph [k {:keys [styles]}]]
                 (if styles
                   (reduce (fn [graph s]
                             (dep/depend graph k s))
                           graph
                           styles)
                   graph))
               (dep/graph)
               styles)]
    (reduce
     (fn [styles k]
       (assoc styles k (expand-styles styles (get styles k))))
     styles
     (dep/topo-sort graph))))

(defn write-attr-key
  [kw]
  (-> kw
      (name)
      (str/escape {\- "_"})))

(defn write-attr-value
  [attr]
  (str "\""
       (str/escape
        (cond
          (keyword? attr) (write-attr-key attr)
          :else (str attr))
        {\" "\\\""})
       "\""))

(defn attr-list
  [attrs]
  (map (fn [[k v]] (str (write-attr-key k) "=" (write-attr-value v))) attrs))

(defn write-attr-list
  [attrs]
  (str (str/join "\n" (attr-list attrs)) "\n"))

(defn write-attr-list-block
  [attrs]
  (str "[\n"
       (str/join "\n," (attr-list attrs))
       "\n]"))

(defn write-style-block
  [known-styles style]
  (if style
    (str
     " "
     (write-attr-list-block (expand-styles known-styles style)))
    ""))

(defn write-node
  [known-styles {:keys [node-id style]}]
  (str (write-attr-key node-id)
       (write-style-block known-styles style)))

(defn write-edge
  [known-styles {:keys [source-node-id target-node-id style]}]
  (str (write-attr-key source-node-id)
       " -> "
       (write-attr-key target-node-id)
       (write-style-block known-styles style)))

(defn write-styleblock
  [known-styles {:keys [type style]}]
  (str
   (write-attr-key type)
   (write-style-block known-styles style)))

(declare write-block)

(defn write-child
  [known-styles [type data]]
  (case type
    :node (write-node known-styles data)
    :edge (write-edge known-styles data)
    :style (write-styleblock known-styles data)
    (write-block known-styles data)))

(defn write-block
  [known-styles {:keys [type name style children]}]
  (str (write-attr-key type)
       (if name
         (str " "
              (write-attr-key name))
         "")
       " {\n"
       (write-attr-list (expand-styles known-styles style))
       (str/join "\n" (map (fn [child] (write-child known-styles child)) children))
       "\n}"))

(defn write-graph
  [graph]
  (let [known-styles (build-known-styles (get graph :styles {}))]
    (write-block known-styles (:graph graph))))

(defn write
  [graph]
  (write-graph graph))
