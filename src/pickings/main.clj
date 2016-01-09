(ns pickings.main
  (:gen-class)
  (:require [pickings.core :as core]
            [com.stuartsierra.component :as component]))

(defn -main
  [& args]
  (println "Hi!")
  (component/start (core/new-system)))