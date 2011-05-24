(ns Hafni.swing.menu
  (:use (Hafni utils) 
        (Hafni.swing component))
  (:import (javax.swing JMenu JMenuBar 
                        JMenuItem JToolBar)))

(defn- add-content [cont x]
  (dorun (map #(if (and (coll? %) (empty? %))
                 (.addSeparator cont)
                 (.add cont (component %))) x)))

(defn menu 
  "Create a JMenu
Fields:
  :content - content of menu | [Component]
  :mnemonic - mnemonic key (see docstring for Hafni.swing.action/action) | Char"
  [title & options]
  (let [m (JMenu.)
        arrs {:mnemonic #(.setMnemonic m (int %))
              :content (partial add-content m)}]
    (.setText m title)
    (init-comp m arrs nil (parse-options options))))

(defn menu-bar
  "Create a JMenuBar
Fields:
  :content - content of menu bar | [Component]"
  [& options]
  (let [m (JMenuBar.)
        arrs {:content (partial add-content m)}]
    (init-comp m arrs nil (parse-options options))))

(defn tool-bar
  "Create a JToolBar
Fields:
  :content - content of toolbar | [Component]
  :floatable - if the toolbar is detachable | Bool
  :rollover - if true, shows a visual indicator under the cursor | Bool"
  [& options]
  (let [t (JToolBar.)
        arrs {:content (partial add-content t)
              :floatable #(.setFloatable t %)
              :rollover #(.setRollover t %)}]
    (init-comp t arrs nil (parse-options options))))

