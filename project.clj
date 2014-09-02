(defproject phoenix-config "0.1.0-SNAPSHOT"
  :description "Configuration for the Phoenix window management utility in ClojureScript"
  :url "https://github.com/mathias/phoenix-config"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild { :builds [{:source-paths ["src"]
                         :compiler {:output-to "target/phoenix.js"
                                    :optimizations :whitespace
                                    :pretty-print true}}]})