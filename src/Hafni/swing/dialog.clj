(ns Hafni.swing.dialog
  (:use (Hafni utils)
        (Hafni.swing component))
  (:import javax.swing.filechooser.FileFilter
           (javax.swing JFileChooser JOptionPane)))

(defn show-message-dialog 
  "Used internally.
Options:
  :parent - the dialogs parent | Component
  :message - the message to be shown | Object
  :title - the title of the dialog | String" 
  [message_type & options]
  (let [opts (parse-options options)]
    (JOptionPane/showMessageDialog (component (:parent opts)) (:message opts) (:title opts) message_type)))

(defn message 
  "Show a simple message dialog."
  [msg]
  (show-message-dialog JOptionPane/INFORMATION_MESSAGE :parent nil :message msg))

(defn info-message 
  "Show an info message dialog.
See (doc show-message-dialog) for options"
  [& options]
  (apply show-message-dialog JOptionPane/INFORMATION_MESSAGE options))

(defn warning-message 
  "Show a warning message dialog.
See (doc show-message-dialog) for options"
  [& options]
  (apply show-message-dialog JOptionPane/WARNING_MESSAGE options))

(defn error-message 
  "Show an error message dialog.
See (doc show-message-dialog) for options"
  [& options]
  (apply show-message-dialog JOptionPane/ERROR_MESSAGE options))

(defn plain-message 
  "Show a plain message dialog.
See (doc show-message-dialog) for options"
  [& options]
  (apply show-message-dialog JOptionPane/PLAIN_MESSAGE options))

(defn text-input-dialog 
  "Open an input dialog
The dialog will contain a text field,
this function will return the value
of that field when the user press OK.
Returns nil when the dialog is cancelled.

Options:
  :parent - parent of dialog | Component
  :message - message of dialog | Object"
  [& options]
  (let [opts (parse-options options)]
    (JOptionPane/showInputDialog (:parent opts) (:message opts))))

(defn adv-input-dialog 
  "Advanced version of text-input-dialog
Options:
  :parent - parent of dialog | Component
  :message - message of dialog | Object
  :title - title of dialog | String
  :possibilities - available choosable options. If this is present, 
                   the text input of the dialog will turn into a combo box.
                   It must be possible to call into-array on the input | [Object]
  :init_value - if :possibilities is present, set the initial chosen value.
                If :possibilities isn't present, do nothing. | Object"
  [& options]
  (let [opts (parse-options options)]
    (JOptionPane/showInputDialog (component (:parent opts)) (:message opts) (:title opts)
                                 JOptionPane/QUESTION_MESSAGE
                                 nil (if-let [p (:possibilities opts)]
                                       (into-array p)
                                       nil)
                                 (:init_value opts))))

(javax.swing.UIManager/put "FileChooser.readOnly" true)

(defn file-to-map 
  "Convert a java.io.File into a map,
the function returns a 'file-map' with form:
{:path String :name String :dir Bool}
(:dir is true if file depicts a directory)"
  [file]
  {:path (.getPath file)
   :name (.getName file)
   :dir (.isDirectory file)})

(defn file-filter 
  "Create a FileFilter.
All options are necessary.
Options:
  :accept - a function taking a file-map (see (doc file-to-map))
            returning true if the file should be shown,
            and vice versa for false.
  :desc - description of this filter, will show up in the 
          filter-selection box when opening a file choosing dialog."
  [& options]
  (let [opts (parse-options options)
        file_filter (proxy [FileFilter] []
                      (accept [file]
                              (if ((:accept opts) (file-to-map file))
                                  true false))
                      (getDescription []
                                      (:desc opts)))]
    file_filter))

(let [file_chooser (JFileChooser.)]
  (defn choose-file 
    "Used internally.
If :multiselection is true, returns a (lazy) list
of chosen files (even if only one file where chosen).
If false, simply returns the chosen file.
If no files where chosen, returns nil.
Options:
  :multiselection - if true, allows the user to select 
                    multiple files | Bool
  :dir - the initial directory of the chooser
         (see Hafni.swing.utils/file) | File
  :filter - see file-filter | FileFilter
  :parent - the parent of the dialog | Component"
    [f options]
    (let [opts (parse-options options)]
      (.setSelectedFile file_chooser nil)
      (when (contains? opts :multiselection) (.setMultiSelectionEnabled file_chooser (:multiselection opts)))
      (when (contains? opts :dir) (.setCurrentDirectory file_chooser (:dir opts)))
      (when (contains? opts :filter) (dorun (map #(.addChoosableFileFilter file_chooser %) (:filter opts))))
      (let [approved (f (component (:parent opts)))]
        (when (contains? opts :filter) (dorun (map #(.removeChoosableFileFilter file_chooser %) (:filter opts))))
        (if (= approved JFileChooser/APPROVE_OPTION)
          (if (:multiselection opts)
              (map file-to-map (.getSelectedFiles file_chooser))
              (file-to-map (.getSelectedFile file_chooser)))))))

  (defn open-file 
    "See (doc choose-file)"
    [& options]
    (choose-file #(.showOpenDialog file_chooser %) options))

  (defn save-file 
    "See (doc choose-file)"
    [& options]
    (choose-file #(.showSaveDialog file_chooser %) options)))

