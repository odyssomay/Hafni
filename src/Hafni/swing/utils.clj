(ns Hafni.swing.utils
  (:use clojure.tools.logging)
  (:import (java.awt Font GraphicsEnvironment)))

(def *available-fonts*
  (vec (.getAvailableFontFamilyNames (GraphicsEnvironment/getLocalGraphicsEnvironment))))

(defn font [name size]
  (if-not (some (partial = name) *available-fonts*)
    (error "Font family doesn't exist - using default."))
  (Font. name Font/PLAIN size))

(defmacro color
  "Returns an instance of java.awt.Color.
The three argument version takes red, green, blue.
The one argument version takes a symbol, which is any of:
  black, blue, cyan, darkGray, gray, green, lightGray,
  magenta, orange, pink, red, white, yellow."
  ([r g b]
   `(java.awt.Color. ~r ~g ~b))
  ([name]
   `(load-string (str "java.awt.Color/" name))))
