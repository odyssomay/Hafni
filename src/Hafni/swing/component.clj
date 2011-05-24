(ns Hafni.swing.component
  (:use (Hafni arrow event utils)))

(defprotocol component_p
  (input-arr [this field] "Get the input arrow of field.")
  (output-arr [this field] "Get the output arrow of field.")
  (event [this field] "Get the event of field.")
  (get-comp [this] ))

(defmacro defcomponent 
  [name fields arr_fn event_fn comp_fn]
  `(defrecord ~name (conj ~fields values#)
     component_p
     (input-arr [this field] (>>> (arr (fn [x] 
                                      (swap! ((keyword values#) this) assoc field x)
                                      x))
                               (arr (partial ~arr_fn this field))))
     (output-arr [this field] (arr (ignore #(@((keyword values#) this) field))))
     (event [this field] (~event_fn this field))
     (get-comp [this] (~comp_fn this))))

(defrecord Component [jcomponent arrs values events]
  component_p
  (input-arr [this field] (>>> (arr (fn [x] 
                                      (swap! (:values this) assoc field x)
                                      x))
                               (arr ((:arrs this) field))))
  (output-arr [this field] (arr (ignore #(@(:values this) field))))
  (event [this field] 
         (if (:events this)
             ((:events this) field)))
  (get-comp [this] (:jcomponent this)))

(defn init-comp-opts [component options]
  (init-options options (reduce #(assoc %1 %2 (input-arr component %2)) {} (keys (:arrs component)))))

(defn create-comp [jcomponent in_arrs events]
  (Component. jcomponent in_arrs (atom {}) events))

(defn init-comp [jcomponent in_arrs events options]
  (let [c (create-comp jcomponent in_arrs events)]
    (init-options options (reduce #(assoc %1 %2 (input-arr c %2)) {} (keys in_arrs)))
    c))

(defmacro comp-and-events [c & events+arrs]
  `(let [c# ~c
         ~'this c#]
     (dorun (map #(connect (event c# (first %)) (second %)) ~(parse-options events+arrs)))
     c#))

(defn component [c] 
  (if (isa? (class c) Hafni.swing.component.component_p)
      (get-comp c)
      c))
