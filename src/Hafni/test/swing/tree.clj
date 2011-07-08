(ns Hafni.test.swing.tree
  (:use clj-arrow.arrow
        (Hafni utils) 
        (Hafni.swing action component layout tree view))
  (:import (Hafni.swing.tree tree-model node)))

(defn tree-model-test []
  (let [tree_map {:root "hello" :child [{:root "hello2" :child [{:root "hello2.1"}
                                                                {:root "hello2.2"}]}
                                        {:root "hello3"}]}
        t (comp-and-events (tree :content tree_map)
                           :selected (arr #(println "selected: " %))
                           :right_click (arr #(println "right clicked: "  %)))]
    (frame :content (border-layout :center (scroll-pane t)
                                   :south  (comp-and-events (button :text "change")
                                                            :act (>>> (arr (const {:root "helloagain" 
                                                                                   :child [{:root "helloagain2" :child [{:root "helloagain2.1"}
                                                                                                                        {:root "helloagain2.2"}]}]}))
                                                                      (input-arr t :content))))
           :size 300 300 :dont_exit_on_close)))

(defn tree-test []
  (frame :content ;(javax.swing.JTree. nil);(tree (tree-node "hello" :content [(tree-node "hello2" :content [(tree-node "hello2.1") 
                  ;                                                                 (tree-node "hello2.2")]) 
                  ;                                   (tree-node "hello3")]))
         :size 300 300
         :dont_exit_on_close))
