(ns Hafni.swing.layout
  (:use (Hafni utils arrow event)
        (Hafni.swing component))
  (:import (javax.swing BoxLayout JPanel)
           (java.awt BorderLayout CardLayout FlowLayout)))

(defn border-layout 
  "Create a BorderLayout
Fields:
  :north, :south, :west, :east, :center - content | Component
  :hgap - Horizontal gap between components | Int
  :vgap - Vertical gap between components | Int
Options:
  :cont - container to be used, by default JPanel | Component"
  [& options] 
  (let [opts (parse-options options)
        layout (BorderLayout.)
        cont (if (contains? opts :cont)
                 (component (:cont opts))
                 (JPanel. layout))
        arrs {:north #(.add cont BorderLayout/NORTH %)
              :south #(.add cont BorderLayout/SOUTH %)
              :west #(.add cont BorderLayout/WEST %)
              :east #(.add cont BorderLayout/EAST %)
              :center #(.add cont BorderLayout/CENTER %)
              :hgap #(.setHgap layout %)
              :vgap #(.setVgap layout %)}]
    (init-comp cont arrs nil opts)))

(defn box-layout
  "Create a BoxLayout
Fields:
  :content - content of layout | [Component]
Options:
  :valign - align components vertically, instead of the default horizontal alignment"
  [& options]
  (let [opts (parse-options options)
        cont  (if (contains? opts :cont)
                (component (:cont opts))
                (JPanel.))
        layout (BoxLayout. cont (if (contains? opts :valign)
                                  BoxLayout/Y_AXIS
                                  BoxLayout/X_AXIS))
        arrs {:content (fn [x] (dorun (map #(.add cont (component %)) x)))}]
    (.setLayout cont layout)
    (init-comp cont arrs nil opts)))

(defn card-layout
  "Create a CardLayout.
Fields:
  :content - content of layout | [(String Component)]
  :show - the card to be shown | String
  :hgap - Horizontal gap between components | Int
  :vgap - Vertical gap between components | Int
  
The input-arr of content only adds new components.
The string that accompany each component is an identifier,
when that component should be shown, that same identifier
is sent as argument to the input-arr :show"
  [& options]
  (let [opts (parse-options options)
        layout (CardLayout.)
        cont (if (contains? opts :cont)
                 (component (:cont opts))
                 (JPanel. layout))
        arrs {:content (fn [x] (dorun (map #(.add cont (component (second %)) (first %)) x)))
              :show #(.show layout cont %)
              :hgap #(.setHgap layout %)
              :vgap #(.setVgap layout %)}]
    (init-comp cont arrs nil opts)))

(defn flow-layout
  "Create a FlowLayout
Fields:
  :content - content of layout | [Component]
  :hgap - Horizontal gap between components | Int
  :vgap - Vertical gap between components | Int
  :align - set alignment of components | String : 
      [\"center\" \"leading\" \"trailing\" \"left\" \"right\"]
  "
  [& options]
  (let [opts (parse-options options)
        layout (FlowLayout.)
        cont (JPanel. layout)
        arrs {:content (fn [x] (dorun (map #(.add cont (component %)) x)))
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
    (init-comp cont arrs nil opts)))
