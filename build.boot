#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.5.1"

(set-env!
 :project 'phoenix-config
 :version "0.0.1"
 :dependencies '[[tailrecursion/boot.task   "2.2.4"]
                 [org.clojure/clojurescript "0.0-2371"]]
 :src-paths #{"src/"}
 :out-path "target/")

(require '[tailrecursion.boot.task :refer :all])

(deftask compile-bindings
  "Compile bindings once"
  []
  (cljs :output-path "phoenix.js"))

(deftask watch-bindings
  "Compile config file to JS"
  []
  (comp (watch) (compile-bindings)))
