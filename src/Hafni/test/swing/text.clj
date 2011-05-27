(ns Hafni.test.swing.text
  (:use (Hafni arrow)
        (Hafni.swing component layout text utils view))) 

(defn text-box-test []
  (frame :size 300 300
         :content (flow-layout :content [(comp-and-events (password-field :width 15)
                                                          :act (arr #(println %)))])
         :dont_exit_on_close))

(defn text-area-test []
  (frame :content (scroll-pane (comp-and-events (text-area :text "YEAH" :font (font "Monospaced" 30))
                                                :insert (arr (fn [[x y]] [x (str y ".")]))
                                                :inserted (arr #(println "inserted: \"" % "\""))
                                                :removed (arr #(println "removed: \"" % "\""))))
         :dont_exit_on_close :size 300 300))

(defn text-pane-test []
  (let [sp (comp-and-events (text-pane :text "YEAH" :styles [{:name "1" :size 20 :bold true
                                                              :italic true :underline true :font "Monospaced"}])
                                       :insert (arr (fn [[x y]] [x (str y ".")]))
                                       :inserted (arr #(println "inserted: \"" % "\""))
                                       :removed (arr #(println "removed: \"" % "\"")))]
    ((input-arr sp :style) ["1" 0 3])
    (frame :content (scroll-pane sp)
           :dont_exit_on_close :size 300 300)))

