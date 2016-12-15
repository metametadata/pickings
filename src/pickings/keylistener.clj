(ns pickings.keylistener
  (:require [integrant.core :as ig]
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

(defn -determine-hotkey
  []
  (if (.contains (System/getProperty "os.name") "Windows")
    "control shift V"
    "meta shift V"))

(defmethod ig/init-key :keylistener
  [_ {:keys [hotkey-callback-atom app callback]}]
  ; using such strange pattern (registering provider once) because jkeymaster hangs on stopping provider in REPL
  (let [hotkey-callback-atom (or hotkey-callback-atom (atom nil))]
    (when (nil? @hotkey-callback-atom)
      (keymaster.core/register (keymaster.core/make-provider)
                               (-determine-hotkey)
                               #(@hotkey-callback-atom %)))

    (reset! hotkey-callback-atom (fn [_] (callback app (-get-clipboard-text))))

    ; return atom for future reuse
    {:hotkey-callback-atom hotkey-callback-atom}))

(defmethod ig/halt-key! :keylistener
  [_ {:keys [hotkey-callback-atom]}]
  (reset! hotkey-callback-atom (constantly nil)))