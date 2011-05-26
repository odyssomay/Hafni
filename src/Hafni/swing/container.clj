(ns Hafni.swing.container
  (:use clj-diff.core
        (Hafni utils event)
        (Hafni.swing component))
  (:import java.awt.event.ActionListener
           (javax.swing.event ChangeListener ListSelectionListener)
           (javax.swing DefaultListModel JComboBox JList JSpinner ListSelectionModel SpinnerListModel)))

(defn change-container-content 
  "Change the content of a container,
insert_f should be a function of 2 arguments: the index, and the object to add
remove_f should be a function of 1 arguments: the index to remove
last_items should be an atom that will be used to store the previous items
new_items should be a coll of the new items."
  [insert_f remove_f last_items new_items]
  (let [d (diff @last_items new_items)
        with_removed (reduce #(drop-nth %2 %1) @last_items (reverse (:- d)))
        with_added (diff with_removed new_items)]
       (dorun (map #(remove_f %) (reverse (:- d))))
       (dorun (map (fn [xs]
                       (let [index (inc (first xs))
                             items (rest xs)]
                            (dorun (map #(insert_f index (component %)) (reverse items))))) (:+ with_added))))
  (swap! last_items (const new_items)))

(defn combo-box
  "Create a JComboBox
Fields:
  :content - set content of the combo box | [Component]
  :add_content - add content to the combo box | Component
  :editable - | Bool
  :selected_index - the currently selected index. 
     This field will NOT contain the user's selection. | Int
Events:
  :selected - fires when a selection has been made,
              sends the index of the selection and the object that was selected | [Int Object]"
  [& options]
  (let [opts (parse-options options)
        event (evt)
        listener (reify ActionListener
                   (actionPerformed [this e]
                                    (let [object (.getSource e)]
                                      (event [(let [i (.getSelectedIndex object)] (if (= i -1) nil i))
                                              (.getSelectedItem object)]))))
        box (JComboBox.)
        last_items (atom [])
        arrs {:content (partial change-container-content #(.insertItemAt box %2 %1) #(.removeItemAt box %) last_items)
              :add_content #(.addItem box (component %))
              :editable #(.setEditable box %)
              :selected_index #(.setSelectedIndex box %)}]
    (.addActionListener box listener)
    (init-comp box arrs {:selected event} opts)))

(defn spinner
  "Create a JSpinner
Fields:
  :content - content of spinner | [Object]
Events:
  :selected - fire when the user changes selection, 
              sends the selected object | Object"
  [& options]
  (let [s (JSpinner.)
        ev (evt)
        listener (reify ChangeListener
                   (stateChanged [_ _] (ev (.getValue s))))
        arrs {:content #(.setModel s (SpinnerListModel. %))}]
    (init-comp s arrs {:selected ev} (parse-options options))))

(defn jlist
  "Create a JList.
Fields:
  :content - the content of the list | [Object]
  :layout - how the content is displayed.
            Available options:
            \"vertical\" (default), \"vertical_wrap\", \"horizontal_wrap\" | String
  :selection_mode - sets the selection mode, available options:
                    \"single\", \"single_interval\" (default), \"multiple_interval\" | String
  :cellh - set the height of each cell | Int
  :cellw - set the width of each cell | Int
Events:
  :selected - fire when the user changes selection,
              sends a vector of all the currently selected indices | [Int]"
  [& options]
  (let [opts (parse-options options)
        ev (evt)
        list_model (DefaultListModel.)
        l (JList. list_model)
        listener (reify ListSelectionListener
                   (valueChanged [_ e]
                                 (ev (vec (.getSelectedIndices l)))))
        last_items (atom [])
        arrs {:content (partial change-container-content #(.add list_model %1 %2) #(.remove list_model %) last_items)
              :layout #(.setLayoutOrientation l (case %
                                                  "vertical" JList/VERTICAL
                                                  "vertical_wrap" JList/VERTICAL_WRAP
                                                  "horizontal_wrap" JList/HORIZONTAL_WRAP))
              :selection_mode #(.setSelectionMode l (case %
                                            "single" ListSelectionModel/SINGLE_SELECTION
                                            "single_interval" ListSelectionModel/SINGLE_INTERVAL_SELECTION
                                            "multiple_interval" ListSelectionModel/MULTIPLE_INTERVAL_SELECTION))
              :cellh #(.setFixedCellHeight l %)
              :cellw #(.setFixedCellWidth l %)
              }]
    (.setVisibleRowCount l -1)
    (.addListSelectionListener l listener)
    (init-comp l arrs {:selected ev} opts)))

