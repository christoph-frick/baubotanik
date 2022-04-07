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

(defn indent
  [{:keys [indent] :as ctx}]
  (str/join (repeat indent "\t")))

(defn inc-indent
  [ctx]
  (update ctx :indent inc))

(defn attr-key
  [kw]
  (-> kw
      (name)
      (str/escape {\- "_"})))

(defn attr-value
  [attr]
  (str "\""
       (str/escape
        (cond
          (keyword? attr) (attr-key attr)
          :else (str attr))
        {\" "\\\""})
       "\""))

(defn attr-list
  [attrs]
  (map (fn [[k v]]
         (str (attr-key k) "=" (attr-value v)))
       attrs))

(defn attrs
  [ctx attrs]
  (let [spacing (indent ctx)]
    (str (str/join
          "\n"
          (map
           (partial str spacing)
           (attr-list attrs)))
         "\n")))

(defn attr-block
  [ctx attrs]
  (let [spacing (indent (inc-indent ctx))]
    (str "[\n"
         spacing
         (str/join (str "\n" spacing ",") (attr-list attrs))
         "\n"
         (indent ctx)
         "]")))

(defn write-style-block
  [{:keys [known-styles] :as ctx} style]
  (if style
    (str " "
         (attr-block ctx (expand-styles known-styles style)))
    ""))

(defn node
  [ctx {:keys [node-id style]}]
  (str (indent ctx)
       (attr-key node-id)
       (write-style-block ctx style)))

(defn edge
  [ctx {:keys [source-node-id target-node-id style]}]
  (str (indent ctx)
       (attr-key source-node-id)
       " -> "
       (attr-key target-node-id)
       (write-style-block ctx style)))

(defn style
  [ctx {:keys [type style]}]
  (str (indent ctx)
       (attr-key type)
       (write-style-block ctx style)))

(declare block)

(defn child
  [ctx [type data]]
  (case type
    :node (node ctx data)
    :edge (edge ctx data)
    :style (style ctx data)
    (block ctx data)))

(defn block
  [{:keys [known-styles] :as ctx} {:keys [type name style children]}]
  (let [ctx-indent+1 (inc-indent ctx)
        spacing (indent ctx)]
    (str spacing
         (attr-key type)
         (if name
           (str " "
                (attr-key name))
           "")
         " {\n"
         (attrs ctx-indent+1 (expand-styles known-styles style))
         (str/join "\n" (map (partial child ctx-indent+1) children))
         "\n"
         spacing
         "}")))

(defn write
  [graph]
  (let [known-styles (build-known-styles (get graph :styles {}))
        ctx {:known-styles known-styles
             :indent 0}]
    (block ctx (:graph graph))))
