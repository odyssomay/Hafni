(ns Hafni.test.swing.menu
  (:use (Hafni arrow utils) 
        (Hafni.swing action component layout menu view)))

(defn menu-bar-test []
  (let [m (menu "menu 1" :content [(menu-item :text "item 1") []
                                   (menu-item :text "item 2")])]
    (frame :menu_bar (menu-bar :content [m])
           :content (flow-layout 
                      :content [(comp-and-events (button :text "add")
                                                 :act (flow (output-arr m :content) >>> 
                                                            (arr #(conj % (menu-item :text "new item!"))) >>> 
                                                            (input-arr m :content)))
                                (comp-and-events (button :text "remove")
                                                 :act (flow (output-arr m :content) >>>
                                                            (arr butlast) >>>
                                                            (input-arr m :content)))])
           :size 300 300 :dont_exit_on_close)))

(defn toolbar-test []
  (frame :content (border-layout :north (tool-bar :content [(button :text "1")
                                                            (button :text "2")
                                                            (button :text "3")]))
         :size 300 300 :dont_exit_on_close))
