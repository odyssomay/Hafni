(ns Hafni.test.swing.table
  (:use (Hafni arrow)
        (Hafni.swing action component layout table view)))

(defn table-test []
  (let [t (comp-and-events (table :content [[1 2 3] [4 5 6] [7 8 9]] :column_names ["hello"] :fill? true 
                                  :selection_mode "multiple" :row_selection? false :column_selection? true); :cell_selection? true)
                           :selected (arr println))
        b (comp-and-events (button :text "press")
                           :act (flow (output-arr t :content) >>> (arr #(conj % [5 6 7 8])) >>> (input-arr t :content)))]
    (frame :content (border-layout :center (scroll-pane t) :south b)
           :size 500 300 :dont_exit_on_close)))
