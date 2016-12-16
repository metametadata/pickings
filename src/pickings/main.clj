(ns pickings.main
  (:gen-class)
  (:require [pickings.core :as core]
            [pickings.keylistener]
            [integrant.core :as ig]))

; remove default methods to detect undefined lifecycle methods
(remove-method ig/init-key :default)
(remove-method ig/halt-key! :default)
; TODO: add other methods and/or see https://github.com/weavejester/integrant/issues/6

(defn -main
  [& args]
  (println "Hi!")
  (ig/init (core/new-config)))