(ns pickings.logic
  (:require [clojure.core.match :refer [match]]
            [clojure.java.io :as io])
  (:import (java.io BufferedInputStream)
           (javazoom.jl.player Player)))

;;; Model
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

;;; Init
(defn init
  []
  {:file      (-file-path "pickings.txt")
   :delimeter "\n--\n\n"})

;;; Control
(defn -beep
  "Inspired by code from https://github.com/technomancy/lein-play."
  []
  (-> (io/input-stream (io/resource "beep.mp3"))
      BufferedInputStream.
      Player.
      .play))

(defn control
  [model signal dispatch]
  (match signal
         :on-connect
         (when-let [loaded-config (-load-config)]
           (dispatch [:reset loaded-config]))

         [:on-set-file file]
         (-> (dispatch [:set-file file])
             -save-config!)

         :on-reveal-file
         (->> (.getAbsoluteFile (clojure.java.io/file (:file model)))
              .getParentFile
              (.open (java.awt.Desktop/getDesktop)))

         :on-open-file
         (->> (.getAbsoluteFile (clojure.java.io/file (:file model)))
              (.open (java.awt.Desktop/getDesktop)))

         [:on-append text]
         (let [{:keys [file delimeter]} model]
           (spit file (str (clojure.string/trim text) delimeter) :append true)
           (-beep))))

;;; Reconcile
(defn reconcile
  [model action]
  (match action
         [:reset new-model]
         new-model

         [:set-file file]
         (assoc model :file file)))