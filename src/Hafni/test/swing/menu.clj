(ns Hafni.test.swing.menu
  (:use (Hafni.swing action menu view)))

(defn menu-toolbar-test []
  (frame :menu_bar (menu-bar :content [(menu "menu 1" :content [(menu-item :name "item 1")
                                                                (menu-item :name "item 2")])])
         :size 300 300 :dont_exit_on_close))

