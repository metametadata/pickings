(ns pickings.core
  (:require [pickings.keylistener :as keylistener]
            [pickings.ui :as ui]
            [pickings.logic :as logic]
            [pickings.mvsa :as mvsa]
            [com.stuartsierra.component :as component]
            [seesaw.core :as sc]))

(def -app-spec {:init      logic/init
                :view      ui/view
                :control   logic/control
                :reconcile logic/reconcile})

; Shows/hides app window, it's kinda an adapter from MVSA to Component pattern
(defrecord App []
  component/Lifecycle
  (start [this]
    (let [new-this (merge this
                          (mvsa/connect-seesaw (-> -app-spec mvsa/wrap-log) []))]
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