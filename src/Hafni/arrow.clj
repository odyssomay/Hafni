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
  (>>>_int [this dest] "")
  (fst_int [this]))

(defrecord Arrow [f]
  clojure.lang.IFn
  (invoke [this input] (f input))
  Arrow_p
  (>>>_int [this dest] 
       (if (isa? (class this) (class dest))
           (assoc this :f #((:f dest) ((:f this) %)))
           (error ">>> was called, but dest isn't an arrow.")))

  (fst_int [this] (assoc this :f (fn [[x y]] [(this x) y]))))

(defn arr [f]
  (if (isa? (class f) (class (Arrow. identity)))
    f
    (Arrow. f)))

(extend-type clojure.lang.IFn
  Arrow_p
  (>>>_int [this dest]
       (comp dest this))
  (fst_int [this]
           (fn [[x y]]
             [(this x) y])))

(defn >>> [& arrs]
  (case (count arrs)
    1 (first arrs)
    2 (>>>_int (first arrs) (second arrs))
    (>>>_int (first arrs) (apply >>> (rest arrs)))))

(defn <<< [& arrs]
  (apply >>> (reverse arrs)))

(defn fst [arr]
  (fst_int arr))

(defn snd [arr]
  (>>> swap (fst arr) swap))

(defn *** [arr1 arr2]
  (>>> (fst arr1) (snd arr2)))

(defn &&& [arr1 arr2]
  (>>> clone (*** arr1 arr2)))

(defn ||| 
  ([arr1 arr2]
   (||| arr1 arr2 (constantly nil)))
  ([arr1 arr2 arr3]
   (>>> (fst arr1) #(if (first %) (arr2 %) (arr3 %)))))

