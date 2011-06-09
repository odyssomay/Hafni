(ns Hafni.swing.table
  (:use (Hafni event utils)
        (Hafni.swing component view))
  (:import (javax.swing ListSelectionModel)))

(defn table-model 
  "Create a table model (built on AbstractTableModel)
all arguments should be refs. When any of the refs are updated
the model is automatically updated.
See (doc table) for what each argument should contain.
  
NOTE:
  If the user changes a cell, the update will be changed
  accordingly in content."
  [content column_names all_editable? sep_editable?]
  (let [model (proxy [javax.swing.table.AbstractTableModel] []
                (getRowCount [] 
                             (if (empty? @content)
                                 0
                                 (apply max (map count @content))))
                (getColumnCount []
                                (count @content))
                (getValueAt [row column]
                            (component (nth (nth @content column []) row nil)))
                (getColumnName [column]
                               (if-let [name (nth @column_names column  nil)]
                                 name
                                 (proxy-super getColumnName column)))
                (isCellEditable [row column]
                                (if @all_editable?
                                    true
                                    (if (empty? (filter #(partial = [column row]) @sep_editable?)) 
                                      false
                                      true)))
                (setValueAt [value row column]
                            (swap! content
                                   (fn [old_content]
                                     (change-i column #(replace-i row value %) old_content)))))]
    (add-watch content nil (ignore #(.fireTableStructureChanged model)))
    (add-watch column_names nil (ignore #(.fireTableStructureChanged model))) 
    model))

(defn table 
  "Create a JTable.
NOTICE:
  If this component isn't in a scroll-pane,
  the column names wont be showed.
Fields:
  :content - the content of the table, a sequence 
             of columns where each column is a sequence | [[Component]]
  :column_names - The name that is displayed over each column.
                  If not present or not long enough 
                  A, B, C, ... will be displayed. | [String]
  :editable? - if true allows every cell to be edited (default false) | Bool
  :sep_editable? - a sequence of [column row] pairs. Each cell 
                   identified by such a pair is editable. If 
                   :editable? is true, this field has no effect | [[Int Int]]
  :fill? - if true, the table will use the entire height of the currently
           available area. If there isn't enough rows, the remaining space
           will be filled by empty cells (default false) | Bool
Events:
  :selected - fires when the user changes selection. Sends a pair where
              the first value is a vector of selected columns, and
              the second value is a vector of selected rows | [[Int] [Int]]"
  [& options]
  (let [opts (parse-options options)
        content (atom (if (contains? opts :content)
                          (:content opts) []))
        column_names (atom [])
        all_editable? (atom false)
        sep_editable? (atom [])
        model (table-model content column_names all_editable? sep_editable?)
        t (javax.swing.JTable. model)
        selected (evt)
        last_selection (atom nil)
        listener (proxy [java.awt.event.MouseAdapter] []
                   (mousePressed [_]
                                 (let [new_selection [(vec (.getSelectedColumns t)) 
                                                      (vec (.getSelectedRows t))]]
                                   (when (not= @last_selection new_selection)
                                     (selected new_selection)
                                     (swap! last_selection (const new_selection))))))
        arrs {:content #(swap! content (const %))
              :column_names #(swap! column_names (const %))
              :editable? #(swap! all_editable? (const %))
              :sep_editable? #(swap! sep_editable? (const %))
              :selection_mode #(.setSelectionMode t
                                                  (case %
                                                    "single" ListSelectionModel/SINGLE_SELECTION
                                                    "interval" ListSelectionModel/SINGLE_INTERVAL_SELECTION
                                                    "multiple" ListSelectionModel/MULTIPLE_INTERVAL_SELECTION))
              :row_selection? #(.setRowSelectionAllowed t %)
              :column_selection? #(.setColumnSelectionAllowed t %)
              :cell_selection? #(.setCellSelectionEnabled t %)
              :fill? #(.setFillsViewportHeight t %)}]
    (.addMouseListener t listener)
    (init-comp t arrs {:selected selected} opts)))

