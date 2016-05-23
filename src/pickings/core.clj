(ns pickings.core
  (:require [pickings.keylistener :as keylistener]
            [pickings.ui :as ui]
            [pickings.spec :as spec]
            [carry.core :as carry]
            [com.stuartsierra.component :as component]
            [seesaw.core :as sc]))

; Shows/hides app window, it's kinda an adapter from Carry to Component pattern
(defrecord App []
  component/Lifecycle
  (start [this]
    (let [app (carry/app spec/spec)
          view (ui/view (:model app) (:dispatch-signal app))
          new-this (-> this
                       (merge app)
                       (assoc :view view))]
      ((:dispatch-signal app) :on-start)
      (sc/show! (:view new-this))
      new-this))

  (stop [this]
    (sc/dispose! (:view this))
    this))

(defn new-system
  []
  (component/system-map
    :app (->App)
    :keylistener (component/using (keylistener/new-keylistener (fn [this text]
                                                                 ((:dispatch-signal (:app this)) [:on-append text])))
                                  [:app])))