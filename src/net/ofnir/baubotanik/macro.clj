(ns net.ofnir.baubotanik.macro
  (:require
   [clojure.walk :as walk]
   [clojure.string :as str]))

(def ^:const macro-arg-prefix
  "%")

(defn macro-arg
  [sym]
  (symbol (str macro-arg-prefix sym)))

(def ^:const macro-arg-rest
  (macro-arg "&"))

(defn macro-arg?
  [sym]
  (str/starts-with? (str sym) macro-arg-prefix))

(defn macro-arg-rest?
    [sym]
    (= macro-arg-rest sym))

(defn all-macro-args?
  [syms]
  (every? macro-arg? syms))

(defn build-macro-lut
  [macros]
  (into {} (for [[macro replacement] macros]
             [(first macro) [macro replacement]])))

(defn macro-call?
  [macro-lut form]
  (when (sequential? form)
    (get macro-lut (first form))))

(defn- splice
  [marker? replacements-fn seqn]
  (into []
        (mapcat
         (fn [node]
           (if (marker? node)
             (replacements-fn node)
             [node])))
        seqn))

(defn expand-macro-positional
  [macro in out]
  (let [replacements (zipmap (rest macro) (rest in))]
    (walk/prewalk-replace replacements out)))

(defn expand-macro-rest
  [_ in out]
  (let [replacements (rest in)]
    (walk/prewalk
     (fn [node]
       (if (and (sequential? node)
                (.contains ^java.util.Collection node macro-arg-rest))
         (splice macro-arg-rest? (constantly replacements) node)
         node))
     out)))

(defn expand-macro
  [macro in out]
  (if (= macro-arg-rest (second macro))
    (expand-macro-rest macro in out)
    (expand-macro-positional macro in out)))

(def ^:const splice-macro-prefix
    "&")

(defn splice-macro?
    [[macro-name]]
    (str/starts-with? (str macro-name) splice-macro-prefix))

(defn macro-call-and-splice?
  [macro-lut form]
  (when-let [[macro _] (macro-call? macro-lut form)]
    (splice-macro? macro)))

(defn expand-splice-macro
  [macro-lut form]
  (let [[macro replacements] (macro-call? macro-lut form)]
    (expand-macro macro form replacements)))

(defn macro-expand-all
  [macros form]
  (let [macro-lut (build-macro-lut macros)]
    (walk/prewalk
     (fn [node]
       (if-let [[macro replacement] (macro-call? macro-lut node)]
         (expand-macro macro node replacement)
         (if (sequential? node)
           (splice (partial macro-call-and-splice? macro-lut)
                   (partial expand-splice-macro macro-lut)
                   node)
           node)))
     form)))
