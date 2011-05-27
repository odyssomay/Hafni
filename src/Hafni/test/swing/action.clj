(ns Hafni.test.swing.action
   (:use clojure.test
         (Hafni arrow event)
         (Hafni.swing action component layout view)))

(defn button-test []
  (let [b (button :text "")]
    (connectr (event b :act) (flow (output-arr b :name) >>> (arr #(str % "*")) >>> (input-arr b :name)))
    (frame :content b :size 200 80 :title "Hello World!" :dont_exit_on_close)))

(defn button-test2 []
  (frame :content (comp-and-events (button :text "*")
                                   :act (flow (output-arr this :name) >>>
                                              (arr #(str % "*")) >>> (input-arr this :name)))
         :size 200 200 :dont_exit_on_close))

(defn radio-test []
  (frame :content
         (flow-layout :content (button-group (radio-button :text "radio1") (radio-button :text "radio2")))
         :title "Radio Test" :size 300 300 :dont_exit_on_close))

(defn check-box-test []
  (frame :content (comp-and-events (toggle-button :text "press")
                                   :act (arr #(prn %)))
         :size 300 300 :dont_exit_on_close))

