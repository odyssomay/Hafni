(ns Hafni.swing.component
  (:use 
    clj-arrow.arrow
    (Hafni event utils)))

(defprotocol component_p
  (input-arr [this field] "Get the input arrow of field.")
  (output-arr [this field] "Get the output arrow of field.")
  (event [this field] "Get the event of field.")
  (get-comp [this] ))

(defmacro defcomponent 
  "Define a component.
fields must contain values and jcomponent, where 
jcomponent holds the wrapped component and values
should hold"
  [name fields arr_map event_map]
  `(defrecord ~name ~fields
     component_p
     (~'input-arr [~'this field#] (>>> (arr (fn [x#] 
                                                (swap! (:values ~'this) assoc field# x#)
                                                x#))
                                       (arr (get ~arr_map field#))))
     (~'output-arr [this# field#] (arr (ignore #(@(:values this#) field#))))
     (~'event [~'this field#] (get ~event_map field#))
     (~'get-comp [this#] (:jcomponent this#))))

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
  (get-comp [this] (:jcomponent this))
  Object
  (toString [this]
            (str "#<Wrapping " 
                 (class (:jcomponent this))
                 " with values: "
                 @(:values this)
                 ">")))

(defmethod clojure.core/print-method Component 
  [this writer]
  (.write writer (str this)))

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
