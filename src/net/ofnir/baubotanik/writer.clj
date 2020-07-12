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
  (map (fn [[k v]]
         (str (write-attr-key k) "=" (write-attr-value v)))
       attrs))

(defn write-attr-list
  [ctx attrs]
  (let [spacing (indent ctx)]
    (str (str/join
          "\n"
          (map
           #(str spacing %)
           (attr-list attrs)))
         "\n")))

(defn write-attr-list-block
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
         (write-attr-list-block ctx (expand-styles known-styles style)))
    ""))

(defn write-node
  [ctx {:keys [node-id style]}]
  (str (indent ctx)
       (write-attr-key node-id)
       (write-style-block ctx style)))

(defn write-edge
  [ctx {:keys [source-node-id target-node-id style]}]
  (str (indent ctx)
       (write-attr-key source-node-id)
       " -> "
       (write-attr-key target-node-id)
       (write-style-block ctx style)))

(defn write-style
  [ctx {:keys [type style]}]
  (str (indent ctx)
       (write-attr-key type)
       (write-style-block ctx style)))

(declare write-block)

(defn write-child
  [ctx [type data]]
  (case type
    :node (write-node ctx data)
    :edge (write-edge ctx data)
    :style (write-style ctx data)
    (write-block ctx data)))

(defn write-block
  [{:keys [known-styles] :as ctx} {:keys [type name style children]}]
  (let [ctx-ident+1 (inc-indent ctx)
        spacing (indent ctx)]
    (str spacing
         (write-attr-key type)
         (if name
           (str " "
                (write-attr-key name))
           "")
         " {\n"
         (write-attr-list ctx-ident+1 (expand-styles known-styles style))
         (str/join "\n" (map (fn [child] (write-child ctx-ident+1 child)) children))
         "\n"
         spacing
         "}")))

(defn write-graph
  [graph]
  (let [known-styles (build-known-styles (get graph :styles {}))
        ctx {:known-styles known-styles
             :indent 0}]
    (write-block ctx (:graph graph))))

(defn write
  [graph]
  (write-graph graph))
