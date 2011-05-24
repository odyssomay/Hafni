(ns Hafni.swing.test
  (:use (Hafni arrow event utils)
        (Hafni.swing view component action layout menu container text utils)))

(defn button-test []
  (let [b (button :name "")]
    (connectr (event b :act) (flow (output-arr b :name) >>> (arr #(str % "*")) >>> (input-arr b :name)))
    (frame :content (panel [b]) :size 200 80 :title "Hello World!" :dont_exit_on_close)))

(defn button-test2 []
  (frame :content (comp-and-events (button :name "*")
                                   :act (flow (output-arr this :name) >>> 
                                              (arr #(str % "*")) >>> (input-arr this :name)))
         :size 200 200 :dont_exit_on_close))

(defn radio-test []
  (frame :content 
         (panel (button-group (radio-button :name "radio1") (radio-button :name "radio2"))) 
         :title "Radio Test" :size 300 300 :dont_exit_on_close))

(defn check-box-test []
  (frame :content (comp-and-events (toggle-button :name "press")
                                   :act (arr #(prn %)))
         :size 300 300 :dont_exit_on_close))

(defn layout-test []
  (let [b (button :name "Hello CENTER")
        i (atom 0)
        layout
         (border-layout :south (button :name "Hello SOUTH")
                        :west (button :name "Hello WEST") :east (button :name "Hello EAST")
                        :center b)]
    (connectr (event b :act) (>>> (arr (ignore #(button :name (str "Hello " (swap! i inc)))))
                                 (input-arr layout :north)))
    (frame :content layout
           :title "Layout test"
           :size 500 300
           :dont_exit_on_close)))

(defn boxlayout-test []
  (frame :content (box-layout :valign :content [(button :name "Hello 1") (button :name "Hello 2")])
         :size 300 300 :dont_exit_on_close))

(defn label-test []
  (frame :content (panel [(label :icon (icon :url "http://clojure.org/space/showimage/clojure-icon.gif"))])
         :size 300 300 :dont_exit_on_close))

(defn label-test2 []
  (let [i (atom 0)
        l (label :text "Pressed 0")]
    (frame :content 
           (box-layout :valign 
                       :content [l (comp-and-events (button :name "press!")
                                                    :act (>>> (arr (ignore #(str "Pressed " (swap! i inc)))) (input-arr l :text)))])
           :size 300 300 :dont_exit_on_close)))

(defn card-layout-test []
  (let [cl_url "http://clojure.org/space/showimage/clojure-icon.gif"
        ha_url "http://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Haskell-Logo.svg/120px-Haskell-Logo.svg.png"
        card (card-layout :content [["clojure" (label :text "clojure" :icon (icon :url cl_url))]
                                    ["haskell" (label :text "haskell" :icon (icon :url ha_url))]])
        box    (box-layout :valign
                           :content [card (box-layout :content [(comp-and-events (button :name "clojure")
                                                                                         :act (>>> (arr (const "clojure"))
                                                                                                   (input-arr card :show)))
                                                                (comp-and-events (button :name "haskell")
                                                                                         :act (>>> (arr (const "haskell"))
                                                                                                   (input-arr card :show)))])])]
           (frame :content box :size 200 150 :location 300 300 :dont_exit_on_close)))

(defn combo-box-test []
  (let [combo (comp-and-events (combo-box :content ["hello" "world" "whats" "up"])
                                   :selected (arr #(prn %)))
        b (comp-and-events (button :name "Press me")
                           :act (flow (output-arr combo :content) >>> (arr #(vec [(first %) (last %) "there"])) >>> (input-arr combo :content)))]
  (frame :content 
         (flow-layout :content [combo b])
         :size 300 300 :dont_exit_on_close)))

(defn menu-toolbar-test []
  (frame :menu_bar (menu-bar :content [(menu "menu 1" :content [(menu-item :name "item 1")
                                                                (menu-item :name "item 2")])])
         :size 300 300 :dont_exit_on_close))

(defn text-box-test []
  (frame :size 300 300
         :content (flow-layout :content [(comp-and-events (password-field :width 15)
                                                          :act (arr #(println %)))])
         :dont_exit_on_close))

(defn text-area-test []
  (frame :content (scroll-pane (comp-and-events (text-area :text "YEAH")
                                                :insert (arr (fn [[x y]] [x (str y ".")]))
                                                :inserted (arr #(println "inserted: \"" % "\""))
                                                :removed (arr #(println "removed: \"" % "\""))))
         :dont_exit_on_close :size 300 300))

(import 'java.awt.Color)

(defn text-pane-test []
  (let [sp (comp-and-events (text-pane :text "YEAH" :styles [{:name "1" :color Color/blue :size 20 :bold true 
                                                              :italic true :underline true}])
                                       :insert (arr (fn [[x y]] [x (str y ".")]))
                                       :inserted (arr #(println "inserted: \"" % "\""))
                                       :removed (arr #(println "removed: \"" % "\"")))]
    ((input-arr sp :style) ["1" 0 3])
    (frame :content (scroll-pane sp)
           :dont_exit_on_close :size 300 300)))

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
