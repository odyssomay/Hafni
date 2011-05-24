(ns Hafni.swing.view
  (:use clj-diff.core
        clojure.contrib.logging
        (Hafni event utils arrow)
        (Hafni.swing component))
  (:import (java.awt.event ActionListener)
           (javax.swing ImageIcon JComboBox JFrame JLabel JPanel JScrollPane)
           (java.net URL))) 

(defn frame
  "Create and show a java swing frame
Fields:
  :title - title of frame | String
  :content - content of frame | Component
  :icon - path to icon for the frame (typical size is 16x16) | String 
  :size - Size of frame | (Int, Int)
  :location - Location of frame | (Int, Int)
  :menu_bar - menu bar of frame | Component

Options:
  :dont_show - Do not show the frame upon creation.
  :dont_exit_on_close - Do not exit the application when the frame closes."
    [& options]
    (let [opts (parse-options options)
          fr (JFrame.)
          arrs {:title #(.setTitle fr %)
                 :content #(.setContentPane fr (component %))
                 :icon #(.setIconImage fr (component %))
                 :size #(.setSize fr (first %) (second %))
                 :location #(.setLocation fr (first %) (second %))
                 :menu_bar #(.setJMenuBar fr (component %))}]
         (if-not (contains? opts :dont_show) (.setVisible fr true))
         (if-not (contains? opts :dont_exit_on_close) 
                 (.setDefaultCloseOperation fr JFrame/EXIT_ON_CLOSE))
         (init-comp fr arrs nil opts)))

(defn panel 
  "DONT USE - use a layout instead
  
  Create a java panel.
Fields:
  :content - content of panel | Component or [Component]
  :layout - layout of panel | Layout"
  [content & options]
  (let [opts (parse-options options)
        pan (JPanel.)]
    (if (coll? content)
      (dorun (map #(.add pan (component %)) content))
             (.add pan (component content)))
    pan))

(defn icon
  "Create a java Icon.
Note that one of :path or :url MUST be given. 
Fields:
  :desc - description of icon | String
Options:
  :path - path to an icon | String
  :url - url to an icon | String"
  [& options]
  (let [opts (parse-options options)
        ico (ImageIcon. (condp #(contains? %2 %1) opts
                               :path (:path opts)
                               :url (URL. (:url opts))
                               (error "Neither :path nor :url was given to icon.")))
        arrs {:desc #(.setDescription ico %)}]
    (init-comp ico arrs nil opts)))

(defn label
  "Create a java label.
Fields:
  :text - the labels text | String
  :icon - path to an icon | String"
  [& options]
  (let [opts (parse-options options)
        l (JLabel.)
        arrs {:text #(.setText l %)
              :icon #(.setIcon l (component %))}]
    (init-comp l arrs nil opts)))

(defn scroll-pane
  ""
  [content]
  (JScrollPane. (component content)))

