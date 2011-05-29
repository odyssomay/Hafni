(ns Hafni.test.swing.dialog
  (:use (Hafni.swing dialog)))

(defn open-file-test []
  (let [file_filter (file-filter :accept #(:dir %) :desc "only directories")]
    (open-file :filter [file_filter])))
