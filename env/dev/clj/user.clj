(ns user
  "Namespace to support hacking at the REPL.

  Usage:
  o (reset) - [re]run the app.
  o In case REPL fails after syntax error, call (refresh) and try again.
  "
  (:require [pickings.core :as core]
            [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; CLJ
(def system (atom nil))

(println "Hi!")

(defn- init []
  (swap! system (constantly (core/new-system))))

(defn- start []
  (swap! system component/start))

(defn- stop []
  (swap! system (fn [s] (when s (component/stop s)))))

(defn- go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))