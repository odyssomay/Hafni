(ns Hafni.swing.layout
  (:use (Hafni utils arrow event)
        (Hafni.swing component view))
  (:import (javax.swing BoxLayout JPanel)
           (java.awt BorderLayout CardLayout FlowLayout GridLayout)))

(defn border-layout 
  "Create a BorderLayout
Fields:
  :north, :south, :west, :east, :center - content | Component
  :hgap - Horizontal gap between components | Int
  :vgap - Vertical gap between components | Int"
  [& options] 
  (let [opts (parse-options options)
        layout (BorderLayout.)
        p (panel :layout layout)
        items (atom {})
        arrs {:north  #(swap! items assoc BorderLayout/NORTH (component %))  ;#(.add cont BorderLayout/NORTH (component %))
              :south  #(swap! items assoc BorderLayout/SOUTH (component %))  ;#(.add cont BorderLayout/SOUTH (component %))
              :west   #(swap! items assoc BorderLayout/WEST (component %))   ;#(.add cont BorderLayout/WEST (component %))
              :east   #(swap! items assoc BorderLayout/EAST (component %))   ;#(.add cont BorderLayout/EAST (component %))
              :center #(swap! items assoc BorderLayout/CENTER (component %)) ;#(.add cont BorderLayout/CENTER (component %))
              :hgap #(.setHgap layout %)
              :vgap #(.setVgap layout %)}]
    (add-watch items nil (fn [_ _ _ new_items]
                           ((input-arr p :content) new_items)))
    (init-comp (component p) arrs nil opts)))

(defn box-layout
  "Create a BoxLayout
Fields:
  :content - content of layout | [Component]
Options:
  :valign - align components vertically, instead of the default horizontal alignment"
  [& options]
  (let [opts (parse-options options)
        p (panel)
        layout (BoxLayout. (component p) (if (contains? opts :valign)
                                           BoxLayout/Y_AXIS
                                           BoxLayout/X_AXIS))
        arrs {:content (input-arr p :content)}]
    (.setLayout (component p) layout)
    (init-comp (component p) arrs nil opts)))

(defn card-layout
  "Create a CardLayout.
Fields:
  :content - content of layout | [(String Component)]
  :show - the card to be shown | String
  :hgap - Horizontal gap between components | Int
  :vgap - Vertical gap between components | Int
  
The input-arr of content only adds new components that can be shown,
but doesn't show them.
The string that accompany each component is an identifier,
when that component should be shown, that same identifier
is sent as argument to the input-arr :show"
  [& options]
  (let [opts (parse-options options)
        layout (CardLayout.)
;        cont (JPanel. layout)
        p (panel :layout layout)
        arrs {:content (>>> (arr #(map reverse %)) (input-arr p :content))
              :show #(.show layout (component p) %)
              :hgap #(.setHgap layout %)
              :vgap #(.setVgap layout %)}]
    (init-comp (component p) arrs nil opts)))

(defn flow-layout
  "Create a FlowLayout
Fields:
  :content - content of layout | [Component]
  :hgap - Horizontal gap between components | Int
  :vgap - Vertical gap between components | Int
  :align - set alignment of components | String : 
      [\"center\" \"leading\" \"trailing\" \"left\" \"right\"]"
  [& options]
  (let [opts (parse-options options)
        layout (FlowLayout.)
        p (panel :layout layout)
        arrs {:content (input-arr p :content)
              :hgap #(.setHgap layout %)
              :vgap #(.setVgap layout %)
              :align #(.setAlignment layout
                             (case %
                               "center" FlowLayout/CENTER
                               "leading" FlowLayout/LEADING
                               "trailing" FlowLayout/TRAILING
                               "left" FlowLayout/LEFT
                               "right" FlowLayout/RIGHT
                               (throw (Exception. ""))))}]
    (init-comp (component p) arrs nil opts)))

(defn grid-layout
  "Create a GridLayout
Fields:
  :content - content of layout | [Component]
  :hgap - Horizontal gap between components | Int
  :vgap - Vertical gap between components | Int"
  [rows cols & options]
  (let [opts (parse-options options)
        layout (GridLayout. rows cols)
        p (panel :layout layout)
        arrs {:content (input-arr p :content)
              :hgap #(.setHgap layout %)
              :vgap #(.setVgap layout %)}]
    (init-comp (component p) arrs nil opts)))
