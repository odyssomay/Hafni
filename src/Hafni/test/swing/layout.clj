(ns Hafni.test.swing.layout
  (:use (Hafni arrow event utils)
        (Hafni.swing action component layout view)))

(defn border-layout-test []
  (let [b (button :text "Hello CENTER")
        i (atom 0)
        layout
         (border-layout :south (button :text "Hello SOUTH")
                        :west (button :text "Hello WEST") :east (button :text "Hello EAST")
                        :center b)]
    (connectr (event b :act) (>>> (arr (ignore #(button :text (str "Hello " (swap! i inc)))))
                                 (input-arr layout :north)))
    (frame :content layout
           :title "Layout test"
           :size 500 300
           :dont_exit_on_close)))

(defn box-layout-test []
  (frame :content (box-layout :valign :content [(button :text "Hello 1") (button :text "Hello 2")])
         :size 300 300 :dont_exit_on_close))
         
(defn card-layout-test []
  (let [cl_url "http://clojure.org/space/showimage/clojure-icon.gif"
        ha_url "http://upload.wikimedia.org/wikipedia/commons/thumb/1/1c/Haskell-Logo.svg/120px-Haskell-Logo.svg.png"
        card (card-layout :content [["clojure" (label :text "clojure" :icon (icon :url cl_url))]
                                    ["haskell" (label :text "haskell" :icon (icon :url ha_url))]])
        box    (box-layout :valign
                           :content [card (box-layout :content [(comp-and-events (button :text "clojure")
                                                                                         :act (>>> (arr (const "clojure"))
                                                                                                   (input-arr card :show)))
                                                                (comp-and-events (button :text "haskell")
                                                                                         :act (>>> (arr (const "haskell"))
                                                                                                   (input-arr card :show)))])])]
           (frame :content box :size 200 150 :location 300 300 :dont_exit_on_close)))

