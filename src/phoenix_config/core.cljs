;; This is a self-documenting configuration in ClojureScript for
;; [Phoenix.app](https://github.com/sdegutis/Phoenix), a lightweight
;; scriptable OSX window manager.
;;
;; Since Phoenix is with JS, this project is written in
;; ClojureScript and compiled to JavaScript. ClojureScript provides
;; some niceties including functional composition to build up window
;; movement functions rather than repetition in each function.
;;
;; ## Usage
;;
;; Install Phoenix.app, and convert this file (`src/phoenix_config/core.cljs`) to JavaScript, for use with Phoenix.app using:
;;
;; <pre><code>lein cljsbuild once
;; </code></pre>
;;
;; in the project directory.
;;
;; Then, symlink the configuration file into place:
;;
;; <pre><code>ln -s ~/path/to/phoenix_config/target/phoenix.js ~/.phoenix.js
;; </code></pre>
;;
;; When Phoenix.app is run, it should pick up and use the config file. Et voila!

(ns phoenix-config.core)

;; ## Phoenix Globals

(def Window js/Window)
(def api js/api)

;; ## Math helpers

(defn round [num] (.round js/Math num))
(defn half [num] (/ num 2))

;; ## Movement functions

(defn push-left []
  (let [win (.focusedWindow Window)
        frame (.frame win)
        screen (.. win screen frameWithoutDockOrMenu)]
    (aset frame "x" (.-x screen))
    (aset frame "y" (.-y screen))

    (aset frame "width" (round (half (.-width screen))))
    (aset frame "height" (.-height screen))

    (.setFrame win frame)))

(defn push-right []
  (let [win (.focusedWindow Window)
        frame (.frame win)
        screen (.. win screen frameWithoutDockOrMenu)]
    (aset frame "x" (+ (.-x screen) (half (.-width screen))))
    (aset frame "y" (.-y screen))

    (aset frame "width" (round (half (.-width screen))))
    (aset frame "height" (.-height screen))

    (.setFrame win frame)))

(defn center-window []
  (let [win (.focusedWindow Window)
        frame (.frame win)
        screen (.. win screen frameWithoutDockOrMenu)]
    (aset frame "x" (/ (.-width screen) 4))
    (aset frame "y" (.-y screen))

    (aset frame "width" (round (half (.-width screen))))
    (aset frame "height" (.-height screen))

    (.setFrame win frame)))

(defn fullscreen []
  (let [win (.focusedWindow Window)
        screen (.. win screen frameWithoutDockOrMenu)]
    (.setFrame win screen)))

(def mash (js/Array. "ctrl" "alt" "cmd"))

(.bind api "left" mash push-left)
(.bind api "right" mash push-right)
(.bind api "c" mash center-window)
(.bind api "m" mash fullscreen)
