(defproject pickings "0.7.0"
  :description "Press global hotkey to append clipboard text into file."
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [integrant "0.1.4"]
                 [carry "0.7.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [seesaw "1.4.5"]
                 [org.clojars.houshuang/keymaster-clj "0.1.0"]
                 [org.clojars.technomancy/jlayer "1.0"]
                 [org.jcommunique/JCommunique "2.0.0"]]

  :repositories [["jcenter" {:url "http://jcenter.bintray.com"}]]

  :main ^:skip-aot pickings.main

  :clean-targets ^{:protect false} ["target" "out"]

  :profiles {:dev     {:source-paths ["env/dev/clj"]
                       :repl-options {:init-ns user}
                       :dependencies [[org.clojure/tools.namespace "0.2.11"]]}

             :uberjar {:aot         :all
                       :omit-source true}})
