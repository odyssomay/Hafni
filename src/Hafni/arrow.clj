(ns Hafni.arrow
  (:use clojure.tools.logging))

(defn clone [x]
  [x x])

(defn swap [[x y]]
  [y x])

(defn- iarr [a f]
  (assoc a :f f))

(defmacro flow [& flows]
  `(-> ~(first flows) ~@(partition 2 (rest flows))))

(defprotocol Arrow_p 
  (>>> [this dest] "")
  (<<< [this src] "")
  (fst [this])
  (snd [this])
  (*** [this a])
  (&&& [this a])
  (||| [this arr1 arr2] [this arr1]))

(defrecord Arrow [f]
  clojure.lang.IFn
  (invoke [this input] ((:f this) input))
  Arrow_p
  (>>> [this dest] 
       (if (isa? (class this) (class dest))
           (assoc this :f #((:f dest) ((:f this) %)))
           (error ">>> was called, but dest isn't an arrow.")))

  (<<< [this src]
       (if (isa? (class this) (class src))
           (>>> src this)
           (error "<<< was called, but src isn't an arrow.")))

  (fst [this] (assoc this :f (fn [[x y]] [(this x) y])))
  (snd [this] (assoc this :f (fn [[x y]] [x (this y)])))

  (*** [this a]
       (if (isa? (class this) (class a))
           (>>> (fst this) (snd a))
           (error "*** was called, but a isn't an arrow")))

  (&&& [this a] 
       (if (isa? (class this) (class a))
           (assoc this :f (fn [x] ((*** this a) [x x])))
           (error "&&& was called, but a isn't an arrow.")))

  (||| [this arr1 arr2]
       (if (isa? (class this) (class arr1))
         (if (isa? (class this) (class arr2))
           (>>> (fst this) (iarr this #(if (first %) (arr1 %) (arr2 %))))
           (error "||| was called, but arr2 isn't an arrow."))
         (error "||| was called, but arr1 isn't an arrow.")))

  (||| [this arr1] (||| this arr1 (iarr this (fn [_] nil)))))

(defn arr [f]
  (Arrow. f))

