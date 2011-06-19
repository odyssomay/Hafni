(ns Hafni.swing.view
  (:use clj-diff.core
        clojure.tools.logging
        (Hafni event utils arrow)
        (Hafni.swing component container))
  (:import (java.awt.event ActionListener)
           (javax.swing ImageIcon JComboBox JFrame JLabel JPanel JProgressBar JScrollPane)
           (java.net URL))) 

(defn frame
  "Create and show a java swing frame
Fields:
  :title - title of frame | String
  :content - content of frame | Component
  :icon - path to icon for the frame (typical size is 16x16) | String 
  :size - Size of frame | [Int, Int]
  :location - Location of frame | [Int, Int]
  :menu_bar - menu bar of frame | Component

Options:
  :dont_show - Do not show the frame upon creation.
  :dont_exit_on_close - Do not exit the application when the frame closes.
  :pack - makes the frame of such size that the content fits.
          Overrides :size"
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
         (let [c (init-comp fr arrs nil opts)]
           (if (contains? opts :pack) (.pack (component c)))
           c)))

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

(defn mouse-position
  "returns the position of the 
mouse as a pair,"
  []
  (let [location (.getLocation (java.awt.MouseInfo/getPointerInfo))
        x (.getX location)
        y (.getY location)]
    [x y]))

(defn panel
  "Create a JPanel
Fields:
  :content - the content of the panel (changeable).
             Each object can either be a Component, or 
             a pair with a Component and a constraint. | Object
Options:
  :layout - the layout to use | Component"
  [& options]
  (let [opts (parse-options options)
        p (JPanel.)
        last_items (atom [])
        arrs {:content (fn [coll]
                         (.removeAll p) ; aah
                         (dorun 
                           (map #(if (or (vector? %)
                                         (list? %)
                                         (seq? %))
                                   (.add p (component (first %)) (second %))
                                   (.add p (component %))) coll))
                         (.updateUI p)) 
                       ;(partial change-container-content 
                       ;         (fn [index object]
                       ;           (if (or (vector? object)
                       ;                   (list? object)
                       ;                   (seq? object))
                       ;             (.add p (component (first object)) (second object) (int index))
                       ;             (.add p (component object) (int index))))
                       ;         #(.remove p %) last_items)}]
             }]
    (if (contains? opts :layout) (.setLayout p (:layout opts)))
    (init-comp p arrs nil opts)))

(defn progress-bar 
  "Create a JProgressBar.
Fields:
  :min - minimum value | Int
  :max - maximum value | Int
  :value - set the current value, will be truncated if outside :min, :max range | Int
  :text - set the currently displayed text, if not specified, shows a percentage | String
  :indeterminate - If true, shows a bouncing block with no information on progress | Bool"
  [& options]
  (let [opts (parse-options options)
        pb (JProgressBar.)
        arrs {:min #(.setMinimum pb %)
              :max #(.setMaximum pb %)
              :value #(.setValue pb %)
              :text #(.setString pb %)
              :indeterminate #(if %
                                (do (.setStringPainted pb false)
                                    (.setIndeterminate pb %))
                                (do (.setStringPainted pb true)
                                    (.setIndeterminate pb %)))}]
    (.setStringPainted pb true)
    (init-comp pb arrs nil opts)))

(defn scroll-pane
  ""
  [content]
  (JScrollPane. (component content)))

