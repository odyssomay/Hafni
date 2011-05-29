(ns Hafni.swing.utils
  (:use clojure.tools.logging)
  (:import java.io.File
           (java.awt Color Font GraphicsEnvironment)))

(def *available-fonts*
  (vec (.getAvailableFontFamilyNames (GraphicsEnvironment/getLocalGraphicsEnvironment))))

(def *path-separator* (File/separator))

(defn file 
  "Create a java.io.File
If the single argument function is used, 
the path must use the systems specific separator.
The multi argument functions inserts the separator
between the arguments."
  ([path] (File. path))
  ([parent child] (File. parent child))
  ([parent child & childs] 
   (apply file (File. parent child) (first childs) (rest childs))))

(defn font [name size]
  (if-not (some (partial = name) *available-fonts*)
    (error "Font family doesn't exist - using default."))
  (Font. name Font/PLAIN size))

(defn color
  "Returns an instance of java.awt.Color.
The three argument version takes red, green, blue.
The one argument version takes a string, which is any of:
  black, blue, cyan, dark_gray, gray, green, light_gray,
  magenta, orange, pink, red, white, yellow."
  ([r g b]
   (Color. r g b))
  ([name]
   (case name
     "black" Color/black
     "blue" Color/blue
     "cyan" Color/cyan
     "dark_gray" Color/darkGray
     "gray" Color/gray
     "green" Color/green
     "light_gray" Color/lightGray
     "magenta" Color/magenta
     "orange" Color/orange
     "pink" Color/pink
     "red" Color/red
     "white" Color/white
     "yellow" Color/yellow)))
