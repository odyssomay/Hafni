(ns Hafni.arrow)

(defprotocol Arrow_p 
  (>>> [this dest] "")
  (<<< [this src] "")
  (fst [this])
  (snd [this])
  (*** [this])
  (&&& [this]))

(defrecord Arrow [f]
  clojure.lang.IFn
  (invoke [this input] ((:f this) input))
  Arrow_p
  (>>> [this dest] (assoc this :f #((:f dest) ((:f this) %))))
  (<<< [this src] (>>> src this))
  (fst [this] (assoc this :f (fn [x] [(this x) x])))
  (snd [this] (assoc this :f (fn [x] [x (this x)])))
  (*** [this] (assoc this :f (fn [[x y]] [(this x) (this y)])))
  (&&& [this] (assoc this :f (fn [x] ((*** this) x x)))))
;  (switch [this] (assoc this :f (fn [

(defn arr [f]
  (Arrow. f))

(defmacro flow [& flows]
  `(-> ~(first flows) ~@(partition 2 (rest flows))))
