(set-env!
  :source-paths   #{"src"}
  :dependencies '[
                  [org.clojure/clojure       "1.7.0"]
                  [org.clojure/clojurescript "1.7.228"]
                  [adzerk/boot-cljs          "1.7.170-3"       :scope "test"]])

(require
  '[adzerk.boot-cljs :refer [cljs]])

(deftask compile-bindings
  "Compile bindings once"
  []
  (cljs :source-map true
        :optimizations :simple
        :compiler-options {:pretty-print true}))

(deftask watch-bindings []
  (comp
   (watch)
   (compile-bindings)))
