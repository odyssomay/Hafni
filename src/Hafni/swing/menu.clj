(ns Hafni.swing.menu
  (:use (Hafni utils) 
        (Hafni.swing component container view))
  (:import (javax.swing JMenu JMenuBar 
                        JMenuItem JToolBar SwingConstants)))

(defn- change-menu-content [m last_items new_items]
  (change-container-content #(if (= %2 [])
                                 (.insertSeparator m %1)
                                 (.insert m (component %2) %1)) 
                            #(.remove m %) last_items new_items))

(defn menu 
  "Create a JMenu
Fields:
  :content - content of menu | [Component]
  :mnemonic - mnemonic key (see docstring for Hafni.swing.action/action) | Char"
  [title & options]
  (let [m (JMenu.)
        last_items (atom [])
        arrs {:mnemonic #(.setMnemonic m (int %))
              :content (partial change-menu-content m last_items)}]
    (.setText m title)
    (init-comp m arrs {} (parse-options options))))

(defn menu-bar
  "Create a JMenuBar
Fields:
  :content - content of menu bar | [Component]"
  [& options]
  (let [m (JMenuBar.)
        last_items (atom [])
        arrs {:content (partial change-menu-content m last_items)}]
    (init-comp m arrs {} (parse-options options))))

(defn popup-menu 
  "DOES NOT create a JPopupMenu
Displays menu at the mousepointers position.
Parent can be nil, but will in that case not
close when an item is choosed."
  [parent menu]
  (let [pmenu (.getPopupMenu (component menu))
        mpos (mouse-position)
        x (first mpos)
        y (second mpos)]
    (.show pmenu (component parent) x y)))

(defn tool-bar
  "Create a JToolBar
Fields:
  :content - content of toolbar | [Component]
  :floatable - if the toolbar is detachable | Bool
  :rollover - if true, shows a visual indicator under the cursor | Bool"
  [& options]
  (let [t (JToolBar.)
        last_items (atom [])
        arrs {:content (partial change-menu-content t last_items)
              :floatable #(.setFloatable t %)
              :rollover #(.setRollover t %)}]
    (init-comp t arrs {} (parse-options options))))

