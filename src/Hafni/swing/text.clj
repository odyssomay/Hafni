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

(defn- event-to-string [e]
  (.getText (.getDocument e) (.getOffset e) (.getLength e)))

(defn- document-listener [ev_inserted ev_removed]
  (reify DocumentListener
    (changedUpdate [_ e]) 
    (insertUpdate [_ e]
                  (ev_inserted [(.getOffset e) (event-to-string e)]))
    (removeUpdate [_ e]
                  (ev_removed [(.getOffset e) (.getLength e)]))))

(defn- init-text-comp 
  [text_comp options events extra_arrs]
  (let [opts (parse-options options)
        ev_inserted (evt)
        ev_removed (evt)
        listener (document-listener ev_inserted ev_removed)
        arrs {:editable #(.setEditable text_comp %)
              :tab_size #(.setTabSize text_comp %)
              :append #(.append text_comp %)
              :font #(.setFont text_comp %)}]
    (.addDocumentListener (.getDocument text_comp) listener)
    (if (contains? opts :text) (.setText text_comp (:text opts)))
    (init-comp text_comp (merge arrs extra_arrs) 
               (merge {:inserted ev_inserted :removed ev_removed} events) 
               opts)))

(defn text-area 
  "Creates a JTextArea
Fields:
  :editable - set if it is allowed to change the area | Bool
  :tab_size - number of spaces for each tab | Int
  :append - append text to the end of the area | String
  :font - font of the text | Font
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
        ev_insert (evt)
        ev_remove (evt)
        document  (proxy [PlainDocument] []
                    (insertString [offset text a]
                                  (if (connected? ev_insert)
                                      (let [ins (ev_insert [offset text])]
                                        (proxy-super insertString (first ins) (second ins) a))
                                      (proxy-super insertString offset text a)))
                    (remove [offset length]
                            (if (connected? ev_remove)
                                (let [remv (ev_remove [offset length])]
                                  (proxy-super remove (first remv) (second remv)))
                                (proxy-super remove offset length))))]
    (.setDocument ta document)
    (init-text-comp ta options
                    {:insert ev_insert
                     :remove ev_remove} nil)))

(defn- set-attribute [doc style pos]
  "Set attribute with position specified in pos to doc."
  (.setCharacterAttributes doc (first pos) (second pos) style true))

(defn text-pane
  "Creates a JTextPane.
Supports everything that text-area supports.
Fields:
  :style - set the style of a part of the text, arguments are
           the style (must be present in :styles), the offset
           and the length of the change | [String Int Int]
  :styles - add styles to be available, :name must be provided, and is
            used as an identifier to :style. Also see:
               Hafni.swing.utils/color 
            Please note that :font takes the FontFamily as a string - and not
            a java.awt.Font object.
                  | [{:name String :font String :color java.awt.Color :size Int :background java.awt.Color
                      :bold Bool :italic Bool :underline Bool}]"
  [& options]
  (let [opts (parse-options options)
        ev_insert (evt)
        ev_remove (evt)
        sc (StyleContext.)
        document (proxy [DefaultStyledDocument] [sc]
                    (insertString [offset text a]
                                  (if (connected? ev_insert)
                                      (let [ins (ev_insert [offset text])]
                                        (proxy-super insertString (first ins) (second ins) a))
                                      (proxy-super insertString offset text a)))
                    (remove [offset length]
                            (if (connected? ev_remove)
                                (let [remv (ev_remove [offset length])]
                                  (proxy-super remove (first remv) (second remv)))
                                (proxy-super remove offset length))))
        tp (JTextPane. document)
        arrs {:style #(set-attribute document (.getStyle document (first %)) (rest %))
              :styles (fn [coll] 
                        (dorun (map #(let [style (.addStyle sc (:name %) nil)]
                                       (dorun (map (fn [x]
                                                     (condp = (first x)
                                                       :font (.addAttribute style StyleConstants/FontFamily (second x))
                                                       :size (.addAttribute style StyleConstants/FontSize (second x))
                                                       :color (.addAttribute style StyleConstants/Foreground (second x))
                                                       :background (.addAttribute style StyleConstants/Background (second x))
                                                       :bold (.addAttribute style StyleConstants/Bold (second x))
                                                       :italic (.addAttribute style StyleConstants/Italic (second x))
                                                       :underline (.addAttribute style StyleConstants/Underline (second x))
                                                       nil)) 
                                                   %))) 
                                    coll)))}]
    (init-text-comp tp options 
                    {:insert ev_insert
                     :remove ev_remove} arrs)))


