(ns pickings.ui
  (:require [seesaw.core :as sc]
            [clojure.java.io :as io])
  (:import (java.awt FileDialog
                     TrayIcon
                     Toolkit
                     SystemTray)
           (java.awt.event MouseAdapter)))

(defn -to-front!
  [frame]
  (sc/invoke-later
    (.setAlwaysOnTop frame true)
    (.toFront frame)
    (.requestFocus frame)
    (.setAlwaysOnTop frame false)))

(defn -add-mouse-pressed-listener
  [component f]
  (let [listener (proxy [MouseAdapter] []
                   (mousePressed [event]
                     (f event)))]
    (.addMouseListener component listener)
    listener))

(defn -new-tray-icon
  "Will show/hide tray icon depending on frame visibility. Returns created tray icon."
  [frame]
  (let [image (-> (Toolkit/getDefaultToolkit)
                  (.getImage "resources/icon.png"))
        tray-icon (new TrayIcon image "")
        tray (SystemTray/getSystemTray)]
    (.setImageAutoSize tray-icon true)
    (-add-mouse-pressed-listener tray-icon
                                 (fn [_]
                                   (-to-front! frame)))

    (sc/listen frame :window-opened
               (fn [_]
                 (.add tray tray-icon)))

    (sc/listen frame :window-closed
               (fn [_]
                 (.remove tray tray-icon)))
    tray-icon))

(defn -choose-file
  [parent]
  (let [dlg (doto (FileDialog. parent "" FileDialog/LOAD)
              (.setDirectory ".")
              (.setVisible true))
        d (.getDirectory dlg)
        f (.getFile dlg)]
    (when (and d f)
      (.getPath (io/file d f)))))

(defn -listen-to-atom-changes!
  "Calls update once and then on every atom change."
  [frame atom update]

  (sc/listen frame :window-opened
             (fn [_]
               (update @atom)
               (add-watch atom :watcher
                          (fn [_key _atom old-state new-state]
                            (when (not= new-state old-state)
                              (update new-state)))))))

(defn view
  "Returns a frame instance which redraws itself on atom changes and dispatches signals via specified function."
  [model-atom dispatch]
  (let [app-frame (sc/frame :title "Pickings"
                            :resizable? true
                            :on-close :exit)
        tray-icon (-new-tray-icon app-frame)
        file-label (sc/label)
        ;delimeter-label (sc/label)
        hotkey-label (sc/label)
        update-app-frame! (fn [model]
                            (.setToolTip tray-icon (str "Pickings\n" (:file model)))
                            (sc/value! file-label (:file model))
                            ;(sc/value! delimeter-label (pr-str (:delimeter model)))
                            (sc/value! hotkey-label "OS X: command+shift+V; Windows: control+shift+V")
                            (sc/pack! app-frame))
        choose-action (sc/action :handler (fn [_e]
                                            (if-let [path (-choose-file app-frame)]
                                              (dispatch [:on-set-file path])))
                                 :name "Change...")
        reveal-action (sc/action :handler
                                 (fn [_e] (dispatch :on-reveal-file))
                                 :name "Reveal")
        open-action (sc/action :handler
                               (fn [_e] (dispatch :on-open-file))
                               :name "Open")
        header #(sc/label :text % :font {:style :bold :size 15})
        content-frame (sc/vertical-panel
                        :items [(sc/flow-panel :background "aliceblue"
                                               :items [(header "Current file:")
                                                       file-label
                                                       choose-action
                                                       reveal-action
                                                       open-action])
                                ;(sc/flow-panel :items [(header "Delimeter:") delimeter-label])
                                (sc/flow-panel :items [(header "Hotkey:") hotkey-label])])]
    (sc/config! app-frame :content content-frame)
    (-listen-to-atom-changes! app-frame model-atom update-app-frame!)
    app-frame))