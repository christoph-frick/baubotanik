(ns net.ofnir.baubotanik.macro
  (:require
   [clojure.walk :as walk]
   [clojure.string :as str]))

(def macro-arg-prefix
  "%")

(defn macro-arg
  [sym]
  (symbol (str macro-arg-prefix sym)))

(def macro-arg-rest
  (macro-arg "&"))

(defn macro-arg?
  [sym]
  (str/starts-with? (str sym) macro-arg-prefix))

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
    (let [macro (first form)]
      (when (contains? macro-lut macro)
        (get macro-lut macro)))))

(defn expand-macro-positional
  [macro in out]
  (let [replacements (zipmap (rest macro) (rest in))]
    (walk/prewalk-replace replacements out)))

(defn- splice
  [marker replacements seqn]
  (into []
        (mapcat
         (fn [x] (if (= marker x)
                   replacements
                   [x])))
        seqn))

(defn expand-macro-rest
  [_ in out]
  (let [replacements (rest in)]
    (walk/prewalk
     (fn [x]
       (if (and (sequential? x)
                (.contains x macro-arg-rest))
         (splice macro-arg-rest replacements x)
         x))
     out)))

(defn expand-macro
  [macro in out]
  (if (= macro-arg-rest (second macro))
    (expand-macro-rest macro in out)
    (expand-macro-positional macro in out)))

(defn macro-expand-all
  [macros form]
  (let [macro-lut (build-macro-lut macros)]
    (walk/prewalk
     (fn [x]
       (if-let [[macro replacement] (macro-call? macro-lut x)]
         (expand-macro macro x replacement)
         x))
     form)))
