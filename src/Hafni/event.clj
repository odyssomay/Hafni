(ns Hafni.event
  (:use clojure.tools.logging))

(defprotocol event_p 
  (connect [this arrow] "Connect this event to arrow.")
  (connectr [this arrow] [this arrow input] "Connect this event to arrow, and execute the arrow.")
  (connected? [this] "Returns true if the event is connected."))

(defrecord Event [arrow]
  clojure.lang.IFn
  (invoke [this]
          (this nil))
  (invoke [this input] 
          (if @(:arrow this)
              (@(:arrow this) input)
              (warn "An event without connection was called.")))
  event_p
  (connect [this arrow]
           (if @(:arrow this)
               (error "An event's connect function was called but the event is already connected.")
               (swap! (:arrow this) (fn [x] arrow))))
  (connectr [this arrow]
            (connectr this arrow nil))
  (connectr [this arrow input]
            (connect this arrow)
            (this input)
            arrow)
  (connected? [this] (not (nil? @(:arrow this)))))

(defn evt []
  (Event. (atom nil)))
