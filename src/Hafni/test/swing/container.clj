(ns Hafni.test.swing.container
  (:use clj-arrow.arrow
        (Hafni utils)
        (Hafni.swing text layout component container view action)))

(defn combo-box-test []
  (let [combo (comp-and-events (combo-box :content ["hello" "world" "whats" "up"])
                                   :selected (arr #(prn %)))
        b (comp-and-events (button :text "Press me")
                           :act (flow (output-arr combo :content) >>> (arr #(vec [(first %) (last %) "there"])) >>> (input-arr combo :content)))]
  (frame :content
         (flow-layout :content [combo b])
         :size 300 300 :dont_exit_on_close)))
         
(defn jlist-test []
  (let [l (comp-and-events (jlist :content (range 100) :layout "horizontal_wrap" :cellh 30 :cellw 30 :selection_mode "multiple_interval")
                           :selected (arr (ignore #(println "selected!"))))] ;(flow (fst (output-arr this :content)) >>> (arr (fn [[x y]] (drop-nth (inc (first y)) x))) >>> (input-arr this :content)))]
    (frame :content (scroll-pane l)
           :size 300 300 :dont_exit_on_close)))
            
(defn tabbed-pane-test []
  (let [tp (tabbed-pane :content [{:content (text-area) :title "area1"}
                                  {:content (text-area) :title "area2"}
                                  {:content (text-area) :title "area3"}
                                  {}])]
    (frame :content (border-layout :center tp :south (comp-and-events (button :text "close first") :act (flow (output-arr tp :content) >>> (arr #(rest %)) >>> (input-arr tp :content))))
           :size 300 300 :dont_exit_on_close)))
