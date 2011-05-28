(ns Hafni.arrow)

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
  (||| [this arr1 arr2]))

(defrecord Arrow [f]
  clojure.lang.IFn
  (invoke [this input] ((:f this) input))
  Arrow_p
  (>>> [this dest] (assoc this :f #((:f dest) ((:f this) %))))
  (<<< [this src] (>>> src this))
  (fst [this] (assoc this :f (fn [[x y]] [(this x) y])))
  (snd [this] (assoc this :f (fn [[x y]] [x (this y)])))
  (*** [this a] (>>> (fst this) (snd a)))
  (&&& [this a] (assoc this :f (fn [x] ((*** this a) [x x]))))
  (||| [this arr1 arr2] (>>> (fst this) (iarr this #(if (first %) (arr1 %) (arr2 %))))))

(defn arr [f]
  (Arrow. f))

