(ns Hafni.swing.text
  (:use clojure.tools.logging
        (Hafni event utils)
        (Hafni.swing component))
  (:import java.awt.event.ActionListener 
           (java.awt Color Font)
           (javax.swing JPasswordField JTextArea JTextField JTextPane)
           (javax.swing.event DocumentListener)
           (javax.swing.text DefaultStyledDocument PlainDocument StyleConstants StyleContext)))

(defn text-field 
  "Create a JTextField
Fields:
  :editable - If the field should be editable | Bool
  :width - set the width of the field | Int
Options:
  :text - the text of the field | String
Events:
  :act - fires when the user is finished typing,
         data sent is what the user typed | String"
  [& options]
  (let [opts (parse-options options)
        tf (JTextField.)
        ev (evt)
        listener (reify ActionListener
                   (actionPerformed [_ _] 
                                    (ev (.getText tf))))
        arrs {:editable #(.setEditable tf %)
              :width #(.setColumns tf %)}]
    (if-not (contains? opts :width)
      (warn "text-field was called without a width argument"))
    (if (contains? opts :text) (.setText tf (:text opts)))
    (.addActionListener tf listener)
    (init-comp tf arrs {:act ev} opts)))

(defn password-field 
  "Create a JPasswordField
Fields:
  :echo_char - The character to be display on input | Char
  :width - set the width of the field | Int
Events:
  :act - fires when the user is finished typing,
         data sent is what the user typed | String"
  [& options]
  (let [opts (parse-options options)
        pf (JPasswordField.)
        ev (evt)
        listener (reify ActionListener
                   (actionPerformed [_ _] 
                                    (ev (str (.getPassword pf)))))
        arrs {:echo_char #(.setEchoChar pf %)
              :width #(.setColumns pf %)}]
    (if-not (contains? opts :width)
      (warn "password-field was called without a width argument"))
    (.addActionListener pf listener)
    (init-comp pf arrs {:act ev} opts)))

(defn formatted-text-field [& options] )

(defn text-area 
  "Creates a JTextArea
Fields:
  :editable - set if it is allowed to change the area | Bool
  :tab_size - number of spaces for each tab | Int
  :append - append text to the end of the area | String
  :font - font of all the text | [Component]
Options:
  :text - the text of the area | String
Events:
  :inserted - text was inserted, sends the 
              offset and the inserted text | [Int String]
  :removed - text was removed, sends the offset
             and length of the removed text | [Int Int]
  :insert - ADVANCED! 
            When the user inserts text (and before it has been
            added to the area) a pair with the offset
            of the insertion and the text to insert is sent to 
            this event. The return value of this event should be
            of the same form, and will be the offset and text actually
            inserted into the area. If this event isn't connected
            the text is simply inserted as it normally would | [Int String]
  :remove - ADVANCED!
            Like :insert with the difference that the pair holds the
            offset and length of the removal. | [Int Int]"
  [& options]
  (let [opts (parse-options options)
        ta (JTextArea.)
        ev_inserted (evt)
        ev_removed (evt)
        ev_insert (evt)
        ev_remove (evt)
        e_to_str (fn [e] 
                   (.getText (.getDocument e) (.getOffset e) (.getLength e)))
        listener (reify DocumentListener
                   (changedUpdate [_ e]) 
                   (insertUpdate [_ e]
                                 (ev_inserted [(.getOffset e) (e_to_str e)]))
                   (removeUpdate [_ e]
                                 (ev_removed [(.getOffset e) (.getLength e)])))
        document (proxy [PlainDocument] []
                   (insertString [offset text a]
                                 (if-let [ins (ev_insert [offset text])]
                                   (proxy-super insertString (first ins) (second ins) a)
                                   (proxy-super insertString offset text a)))
                   (remove [offset length]
                           (if-let [remv (ev_remove [offset length])]
                             (proxy-super remove (first remv) (second remv))
                             (proxy-super remove offset length))))
        arrs {:editable #(.setEditable ta %)
              :tab_size #(.setTabSize ta %)
              :append #(.append ta %)
              :font #(.setFont ta %)}]
    (.setDocument ta document)
    (.addDocumentListener (.getDocument ta) listener)
    (if (contains? opts :text) (.setText ta (:text opts)))
    (init-comp ta arrs {:inserted ev_inserted
                        :removed ev_removed
                        :insert ev_insert
                        :remove ev_remove} opts)))

(defn- set-attribute [doc style pos]
  "Set attribute with position specified in pos to doc."
  (.setCharacterAttributes doc (first pos) (second pos) style true))

(defn text-pane
  "Similar to text-area
Fields:
  :style - set the style of a part of the text, arguments are
           the style (must be present in :styles), the offset
           and the length of the change | [String Int Int]
  :styles - add styles to be available, :name must be provided, and is
            used as an identifier to :style. Also see:
               Hafni.swing.utils/color 
               Hafni.swing.utils/font
                  | [{:name String :font Component :color java.awt.Color :size Int :background java.awt.Color
                      :bold Bool :italic Bool :underline Bool}]"
  [& options]
  (let [opts (parse-options options)
        ev_inserted (evt)
        ev_removed (evt)
        ev_insert (evt)
        ev_remove (evt)
        e_to_str (fn [e] 
                   (.getText (.getDocument e) (.getOffset e) (.getLength e)))
        listener (reify DocumentListener
                   (changedUpdate [_ e]) 
                   (insertUpdate [_ e]
                                 (ev_inserted [(.getOffset e) (e_to_str e)]))
                   (removeUpdate [_ e]
                                 (ev_removed [(.getOffset e) (.getLength e)])))
        sc (StyleContext.)
        document (proxy [DefaultStyledDocument] [sc]
                   (insertString [offset text a]
                                 (if-let [ins (ev_insert [offset text])]
                                   (proxy-super insertString (first ins) (second ins) a)
                                   (proxy-super insertString offset text a)))
                   (remove [offset length]
                           (if-let [remv (ev_remove [offset length])]
                             (proxy-super remove (first remv) (second remv))
                             (proxy-super remove offset length))))
        tp (JTextPane. document)
        arrs {:editable #(.setEditable tp %)
              :tab_size #(.setTabSize tp %)
              :append #(.append tp %)
              :font #(.setFont tp %)
              :style #(set-attribute document (.getStyle document (first %)) (rest %))
              :styles (fn [coll] 
                        (dorun (map #(let [style (.addStyle sc (:name %) nil)]
                                       (dorun (map (fn [x]
                                                     (println "we got: " x)
                                                     (condp = (first x)
                                                       :font (.addAttribute style StyleConstants/FontFamily (second x))
                                                       :size (.addAttribute style StyleConstants/FontSize (second x))
                                                       :color (.addAttribute style StyleConstants/Foreground (second x))
                                                       :background (.addAttribute style StyleConstants/Background (second x))
                                                       :bold (.addAttribute style StyleConstants/Bold (second x))
                                                       :italic (.addAttribute style StyleConstants/Italic (second x))
                                                       :underline (.addAttribute style StyleConstants/Underline (second x))
                                                       nil)) %))) coll)))}]
    (if (contains? opts :text) (.setText tp (:text opts)))
    (.addDocumentListener (.getDocument tp) listener)
    (init-comp tp arrs {:inserted ev_inserted
                        :removed ev_removed
                        :insert ev_insert
                        :remove ev_remove} opts)))


