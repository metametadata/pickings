(ns user
  "Namespace to support hacking at the REPL.

  Usage:
  o (reset) - [re]run the app.
  "
  (:require [pickings.core :as core]
            [integrant.core :as ig]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs refresh refresh-all]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer :all]))

(println "Hi dev!")

(def system (atom nil))

(defn -start
  []
  ; pass existing system because key provider can be already initialized and cannot be stopped via REPL (it's a bug of keymaster lib)
  (swap! system (comp ig/init core/new-config)))

(defn -stop
  []
  (when @system (ig/halt! @system)))

(defn reset
  []
  (-stop)

  ; this will enforce reloading of the methods and detection of missing methods
  (remove-all-methods ig/init-key)
  (remove-all-methods ig/halt-key!)
  ; TODO: add other methods and/or see https://github.com/weavejester/integrant/issues/6 and https://github.com/weavejester/integrant/issues/8

  ; do not refresh this ns because it stores the previous system value needed on restart
  (set-refresh-dirs "src")

  ; refresh-all is used instead of refresh because we've just removed all the methods and want to find all the definitions
  (refresh-all :after 'user/-start)
  nil)