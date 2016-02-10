(defproject pickings "0.1.0-SNAPSHOT"
  :description "Press global hotkey to append clipboard text into file."
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [seesaw "1.4.5"]
                 [com.stuartsierra/component "0.3.1"]
                 [org.clojars.houshuang/keymaster-clj "0.1.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.craigandera/dynne "0.4.1"]]

  :main ^:skip-aot pickings.main

  :clean-targets ^{:protect false} ["target" "out"]

  :profiles {:dev     {:source-paths ["env/dev/clj"]
                       :repl-options {:init-ns user}
                       :dependencies [[org.clojure/tools.namespace "0.2.10"]]}

             :uberjar {:aot         :all
                       :omit-source true}})
