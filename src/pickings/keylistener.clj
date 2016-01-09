(ns pickings.keylistener
  (:require [com.stuartsierra.component :as component]
            [keymaster.core])

  (:import java.awt.Toolkit)
  (:import (java.awt.datatransfer DataFlavor)))

(defn -get-clipboard-text
  []
  (let [clipboard (.getSystemClipboard (Toolkit/getDefaultToolkit))
        contents (.getContents clipboard nil)
        text? (.isDataFlavorSupported contents DataFlavor/stringFlavor)]
    (if text?
      (.getData clipboard DataFlavor/stringFlavor))))

; TODO: it doesn't work with namespace reloading!
; using such strange pattern because jkeymaster hangs on stopping provider in REPL
(defonce -hotkey-provider (atom nil))
(defonce -hotkey-callback (atom nil))

(defn -register-hotkey-provider
  []
  (when (nil? @-hotkey-provider)
    (reset! -hotkey-provider (keymaster.core/make-provider))
    (keymaster.core/register @-hotkey-provider "meta shift V" #(@-hotkey-callback %))))

(defrecord Keylistener [callback app]
  component/Lifecycle

  (start [this]
    (reset! -hotkey-callback (fn [_] (callback this (-get-clipboard-text))))
    (-register-hotkey-provider)
    this)

  (stop [this]
    (reset! -hotkey-callback (constantly nil))
    this))

(defn new-keylistener
  "Callback will receive |this| and clipboard text when global hotkey is pressed."
  [callback]
  (map->Keylistener {:callback callback}))