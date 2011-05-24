(ns Hafni.swing.action
  (:use (Hafni utils arrow event)
        (Hafni.swing component))
  (:import (javax.swing Action AbstractAction 
                        ButtonGroup
                        JButton JCheckBox JCheckBoxMenuItem
                        JMenuItem
                        JRadioButton JRadioButtonMenuItem
                        JToggleButton 
                        KeyStroke)))

(defn action 
  "Creates a java action.
Fields:
  :name - name/title of action. | String
  :desc - short description of action. (tooltip) | String
  :mnemonic - See http://download.oracle.com/javase/tutorial/uiswing/components/menu.html#mnemonic | Char
  :accelerator - See above and http://download.oracle.com/javase/6/docs/api/javax/swing/KeyStroke.html#getKeyStroke(java.lang.String) | String
  :icon - an icon | Component
  
Events:
  :act - this event triggers when the action is triggered. | nil"
  [& options]
  (let [opts (parse-options options)
        ev (evt)
        jaction (proxy [AbstractAction] []
                       (actionPerformed [e] (ev)))
        arrs     {:name        #(.putValue jaction Action/NAME %)
                  :desc        #(.putValue jaction Action/SHORT_DESCRIPTION %)
                  :mnemonic    #(.putValue jaction Action/MNEMONIC_KEY (int %))
                  :accelerator #(.putValue jaction Action/ACCELERATOR_KEY (KeyStroke/getKeyStroke %))
                  :icon        #(.putValue jaction Action/SMALL_ICON (component %))}]
    (init-comp jaction arrs {:act ev} opts)))

(defn button 
  "See (doc action) for options."
  [& options]
  (let [act (apply action options)]
    (assoc act :jcomponent (JButton. (component act)))))

(defn toggle-button
  "See (doc check-box)"
  [& options]
  (let [act (apply action options)
        checkbox (JToggleButton. (component act))
        ev (evt)]
    (connect (event act :act) (>>> (arr (ignore #(.isSelected checkbox))) (arr ev)))
    (assoc act 
           :jcomponent checkbox
           :events (assoc (:events act) :act ev))))

(defn radio-button
  "See (doc action) for options."
  [& options]
  (let [act (apply action options)]
    (assoc act :jcomponent (JRadioButton. (component act)))))

(defn check-box
  "See (doc action) for options.
Events:
  :act - triggers when the checkbox is pressed. | Bool"
  [& options]
  (let [act (apply action options)
        checkbox (JCheckBox. (component act))
        ev (evt)]
    (connect (event act :act) (>>> (arr (ignore #(.isSelected checkbox))) (arr ev)))
    (assoc act 
           :jcomponent checkbox
           :events (assoc (:events act) :act ev))))

(defn menu-item
  "See (doc action) for options."
  [& options]
  (let [act (apply action options)]
    (assoc act :jcomponent (JMenuItem. (component act)))))

(defn check-box-menu-item
  "See (doc CheckBox)"
  [& options]
  (let [act (apply action options)
        checkbox (JCheckBoxMenuItem. (component act))
        ev (evt)]
    (connect (event act :act) (>>> (arr (ignore #(.isSelected checkbox))) (arr ev)))
    (assoc act 
           :jcomponent checkbox
           :events (assoc (:events act) :act ev))))

(defn radio-button-menu-item
  "See (doc RadioButton)"
  [& options]
  (let [act (apply action options)]
    (assoc act :jcomponent (JRadioButtonMenuItem. (component act)))))

(defn CButtonGroup
  "Used internally, see (doc button-group)"
  [buttons]
  (let [group (ButtonGroup.)]
    (dorun (map #(.add group (component %)) buttons))
    group))

(defn button-group
  [& buttons]
  (CButtonGroup buttons)
  buttons)
