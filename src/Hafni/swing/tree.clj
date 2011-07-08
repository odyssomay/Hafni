(ns Hafni.swing.tree
  (:use clj-diff.core
        (Hafni event utils)
        (Hafni.swing component container))
  (:import (javax.swing JTree)))

;; TODO: make lazy!!!!

(defn tree-node 
  [root & options]
  (let [opts (parse-options options)
        tn (javax.swing.tree.DefaultMutableTreeNode. root)
        last_items (atom [])
        arrs {:content (partial change-container-content #(.insert tn (component %2) %1) #(.remove %) last_items)}]
    (init-comp tn arrs {} opts)))

;; MODEL

(defrecord node [root child]
  Object
  (toString [this]
            (:root this)))

(defrecord tree-model [root listeners]
  javax.swing.tree.TreeModel
  (addTreeModelListener [this l]
                        (swap! (:listeners this) conj l))
  (getChild [this parent index]
            (nth (:child parent) index))
  (getChildCount [this parent]
                 (count (:child parent)))
  (getIndexOfChild [this parent child]
                   (find-i child (:child parent)))
  (getRoot [this]
           @(:root this))
  (isLeaf [this node]
          (empty? (:child node)))
  (removeTreeModelListener [this l]
                           (if-let [index (find-i l @(:listeners this))]
                             (swap! (:listeners this) #(drop-nth index %))))
  (valueForPathChanged [this path new_value]))

(defn- map-to-nodes [m]
  (node. (:root m) (map map-to-nodes (:child m))))

(defn- fire-tree-structure-changed [model nodes]
  (let [root @(:root model)
        e (javax.swing.event.TreeModelEvent. root (into-array [root]))]
    (dorun (map #(.treeStructureChanged % e) @(:listeners model)))))

(defn- tree-path-to-seq [path]
  (->> (.getPath path)
       (map :root)))

;(defn map-to-seq [m]
;  (if (map? m)
;      (interleave (map first m)
;                  (map map-to-seq 
;      (map (fn [x] (prn x) (map #(vec [(first x) (map-to-seq %)]) (second x))) m)
;      m))

(defn tree 
  "Create a JTree
Fields:
  :content - a map with nodes represented as 
             {:root String :child [{:root String :child [...]}]} | Map
Events:
  :selected - when the user changes the selection,
              this event sends the current selection | [[String]]
  :right_click - if the user right-clicks a node the node
                 is selected and this event sends the path 
                 of that node | [String]"
  [& options]
  (let [opts (parse-options options)
        nodes (atom (map-to-nodes (:content opts)))
        model (tree-model. nodes (atom []))
        selected (evt)
        right_click (evt)
        currently_selected (atom [])
        selection_listener (reify javax.swing.event.TreeSelectionListener
                             (valueChanged [this e]
                                           (let [paths (.getPaths e)
                                                 added (filter #(.isAddedPath e %) paths)
                                                 removed (remove #(.isAddedPath e %) paths)]
                                             (dorun (map (fn [r] 
                                                           (swap! currently_selected 
                                                                  #(drop-nth (find-i r %) %))) removed))
                                             (swap! currently_selected concat added)
                                             (->> @currently_selected
                                                  (map tree-path-to-seq)
                                                  selected))))
        t (JTree. model)
        mouse_listener (proxy [java.awt.event.MouseAdapter] []
                         (mouseClicked [e]
                                       (if (javax.swing.SwingUtilities/isRightMouseButton e)
                                         (if-let [path (.getPathForLocation t (.getX e) (.getY e))]
                                           (right_click (tree-path-to-seq path))))))]
    (.addTreeSelectionListener t selection_listener)
    (.addMouseListener t mouse_listener)
    (add-watch nodes nil (ignore #(fire-tree-structure-changed model nil)))
    (let [arrs {:content #(swap! nodes (const (map-to-nodes %)))}]
      (init-comp t arrs {:selected selected
                         :right_click right_click} opts))))

