(ns Hafni.swing.dialog
  (:use (Hafni utils))
  (:import (javax.swing JOptionPane)))

(defn show-message-dialog [message_type & options]
  (let [opts (parse-options options)]
    (JOptionPane/showMessageDialog (component (:parent opts)) (:message opts) (:title opts) message_type)))

(defn message 
  "Show a message dialog.
Options:
  :parent - the dialogs parent | Component
  :message - the message to be shown | Object
  :title - the title of the dialog | String"
  [msg]
  (show-message-dialog JOptionPane/INFORMATION_MESSAGE :parent nil :message msg))

(defn info-message 
  "Show an info message dialog.
See (doc message) for options"
  [& options]
  (apply show-message-dialog JOptionPane/INFORMATION_MESSAGE options))

(defn warning-message 
  "Show a warning message dialog.
See (doc message) for options"
  [& options]
  (apply show-message-dialog JOptionPane/WARNING_MESSAGE options))

(defn error-message 
  "Show an error message dialog.
See (doc message) for options"
  [& options]
  (apply show-message-dialog JOptionPane/ERROR_MESSAGE options))

(defn plain-message 
  "Show a plain message dialog.
See (doc message) for options"
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

