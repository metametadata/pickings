(ns pickings.spec
  (:require [clojure.core.match :refer [match]]
            [clojure.java.io :as io])
  (:import (java.io BufferedInputStream)
           (javazoom.jl.player Player)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn -file-path
  "App stores files in user's home folder."
  [filename]
  (.getPath (io/file (System/getProperty "user.home") filename)))

(def -config-path (-file-path ".pickings-config.edn"))

(defn -load-config
  "Returns loaded config or nil if there's no config."
  []
  (when-let [reader (try
                      (slurp -config-path)
                      (catch Exception _ false))]
    (let [{:keys [file delimeter] :as config} (read-string reader)]
      (assert (string? file) (str "actual: " (pr-str file)))
      (assert (string? delimeter) (str "actual: " (pr-str delimeter)))
      config)))

(defn -save-config!
  [config]
  (spit -config-path (pr-str config))
  config)

(def -initial-model
  {:file      (-file-path "pickings.txt")
   :delimeter "\n--\n\n"})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn -beep
  "Inspired by code from https://github.com/technomancy/lein-play."
  []
  (-> (io/input-stream (io/resource "beep.mp3"))
      BufferedInputStream.
      Player.
      .play))

(defn -control
  [model signal _dispatch-signal dispatch-action]
  (println "signal =" (pr-str signal))
  (match signal
         :on-start
         (when-let [loaded-config (-load-config)]
           (dispatch-action [:reset loaded-config]))

         [:on-set-file file]
         (do
           (dispatch-action [:set-file file])
           (-save-config! @model))

         :on-reveal-file
         (->> (.getAbsoluteFile (clojure.java.io/file (:file @model)))
              .getParentFile
              (.open (java.awt.Desktop/getDesktop)))

         :on-open-file
         (->> (.getAbsoluteFile (clojure.java.io/file (:file @model)))
              (.open (java.awt.Desktop/getDesktop)))

         [:on-append text]
         (let [{:keys [file delimeter]} @model]
           (spit file (str (clojure.string/trim text) delimeter) :append true)
           (-beep))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn -reconcile
  [model action]
  (println "  action =" (pr-str action))
  (match action
         [:reset new-model]
         new-model

         [:set-file file]
         (assoc model :file file)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def spec
  {:initial-model -initial-model
   :control       -control
   :reconcile     -reconcile})