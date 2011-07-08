(ns Hafni.test.swing.view
  (:use clj-arrow.arrow
        (Hafni utils)
        (Hafni.swing action component layout view)))

(defn label-test []
  (frame :content (label :icon (icon :url "http://clojure.org/space/showimage/clojure-icon.gif"))
         ;:size 300 300 
         :dont_exit_on_close :pack))

(defn label-test2 []
  (let [i (atom 0)
        l (label :text "Pressed 0")]
    (frame :content
           (box-layout :valign
                       :content [l (comp-and-events (button :name "press!")
                                                    :act (>>> (arr (ignore #(str "Pressed " (swap! i inc)))) (input-arr l :text)))])
           :size 300 300 :dont_exit_on_close)))

(defn progress-bar-test []
  (let [pb (progress-bar :min 0 :max 20 :value 0 :indeterminate true)
        b (comp-and-events (button :name "inc")
                           :act (flow (output-arr pb :value) >>> (arr #(+ % 10)) >>> (input-arr pb :value)))
        b2 (comp-and-events (button :name "dec")
                            :act (flow (output-arr pb :value) >>> (arr #(- % 10)) >>> (input-arr pb :value)))
        b3 (comp-and-events (button :name "swap indeterminate state")
                            :act (flow (output-arr pb :indeterminate) >>> (arr not) >>> (input-arr pb :indeterminate)))]
    (frame :content (flow-layout :content pb b b2 b3 :align "center")
           :size 300 300 :dont_exit_on_close)))

(defn panel-test []
  (let [p (panel :content (map #(label :text (str %)) (range 10)))
        l (border-layout :center p 
                         :south (comp-and-events (button :text "flip")
                                                 :act (flow
                                                        (output-arr p :content) >>>
                                                        (arr #(cons (last %) (butlast %))) >>>
                                                        (input-arr p :content))))]
    (frame :content l
           :dont_exit_on_close
           :pack)))
