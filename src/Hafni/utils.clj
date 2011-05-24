(ns Hafni.utils)

(defn rmap [f coll]
  (if (coll? coll)
    (map #(rmap f %) coll)
    (f coll)))

(defn parse-options 
  "Creates a hash-map with the collection options.
Keys are assumed to be keywords and keywords with no 
supplied value are given value nil.
Example usage:
  (parse-options [:option1 3 4 1 :option2 :option3 8])
  => {:option1 (3 4 1) :option2 nil :option3 8}"
  [options]
  (let [i (atom true)]
       (apply merge (map #(apply hash-map %) 
                         (map #(if (= (count %) 1)
                                   [(first %) nil]
                                   [(first %) (if (= (count %) 2)
                                                  (last %)
                                                  (rest %))])
                              (partition-by #(and (keyword? %)
                                                  (swap! i not)) options))))))

(defn deref-or-val
  "Returns value of reference.
Example usage:
  (deref-or-val 4)
  => 4
  (deref-or-val  (atom 4))
  => 4"
  [reference]
  (if (isa? (class reference) clojure.lang.ARef)
      (deref reference)
      reference))

(defn assoc-if 
  "Only assoc if the key already exists in coll."
  [coll & kvs]
  (let [kvs (flatten (filter #(contains? coll (first %)) (partition 2 kvs)))]
    (if (empty? kvs)
      coll
      (apply assoc coll kvs))))

(defn ignore 
  "Takes the function f of 0 args and creates a
function with any number of args which are ignored."
  [f] (fn [& _] (f)))

(defn const 
  "Takes a value x and returns a function with any 
number of args and which always evaluates to x."
  [x]
  (ignore (fn [] x)))

(defn init-options [options in_arrs]
  (dorun (map (fn [x]
                  (if (and (second x) (contains? in_arrs (first x)))
                      (((first x) in_arrs) (second x))))
              options)))

(defn drop-nth [n coll]
  (concat (take n coll)
          (rest (drop n coll))))
