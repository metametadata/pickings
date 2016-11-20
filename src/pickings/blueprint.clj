(ns pickings.blueprint
  (:require [clojure.core.match :refer [match]]
            [clojure.java.io :as io])
  (:import (java.io BufferedInputStream)
           (javazoom.jl.player Player)
           (com.notification NotificationFactory NotificationFactory$Location)
           (com.theme ThemePackagePresets)
           (com.notification.manager SimpleManager)
           (com.utils Time)
           (javax.swing SwingUtilities)))

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
  {:file           (-file-path "pickings.txt")
   :delimeter      "\n--\n\n"
   :sound?         true
   :notifications? true})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn -notify
  [text]
  (let [factory (NotificationFactory. (ThemePackagePresets/cleanLight))
        manager (SimpleManager. NotificationFactory$Location/NORTHEAST)
        notification (doto
                       (.buildTextNotification factory "Saved" text)
                       (.setCloseOnClick true))]
    (SwingUtilities/invokeLater
      #(.addNotification manager notification (Time/seconds 1.5)))))

(defn -beep
  "Inspired by code from https://github.com/technomancy/lein-play."
  []
  (-> (io/input-stream (io/resource "beep.mp3"))
      BufferedInputStream.
      Player.
      .play))

(defn -on-signal
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

         :on-toggle-sound
         (do
           (dispatch-action :toggle-sound)
           (-save-config! @model))

         :on-toggle-notifications
         (do
           (dispatch-action :toggle-notifications)
           (-save-config! @model))

         [:on-append text]
         (let [{:keys [file delimeter]} @model]
           (spit file (str (clojure.string/trim text) delimeter) :append true)
           (when (:notifications? @model)
             (-notify text))

           (when (:sound? @model)
             (future-call #(-beep))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn -on-action
  [model action]
  (println "  action =" (pr-str action))
  (match action
         [:reset new-model]
         new-model

         [:set-file file]
         (assoc model :file file)

         :toggle-sound
         (update model :sound? not)

         :toggle-notifications
         (update model :notifications? not)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def blueprint
  {:initial-model -initial-model
   :on-signal     -on-signal
   :on-action     -on-action})