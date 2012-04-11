(defproject mehub "0.1.0"
  :description "GitHub Personal page"
  :dependencies [[org.clojure/clojure "1.4.0-beta3"]
                 [jayq "0.1.0-alpha2"]
                 [hiccups "0.1.1"]]
  :dev-dependencies [[lein-cljsbuild "0.1.6"]]
  :cljsbuild {:builds [{:source-path "src-cljs"
                        :compiler {:optimizations :simple
                                   :pretty-print true
                                   :output-to "resources/js/mehub.js"}}
                       #_{:source-path "src-cljs"
                        :compiler {:optimizations :advanced
                                   :output-to "resources/js/mehub.js"}}]})
