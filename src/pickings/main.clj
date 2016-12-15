(ns pickings.main
  (:gen-class)
  (:require [pickings.core :as core]
            [integrant.core :as ig]))

(defn -main
  [& args]
  (println "Hi!")
  (ig/init (core/new-config)))