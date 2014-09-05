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

(def App js/App)
(def Window js/Window)
(def api js/api)
(def focused-window #(.focusedWindow Window))

;; ClojureScript's `map` has issues, so we use the included Underscore map fn:
(def _map #(.map js/_ %2 %1))
(def _filter #(.filter js/_ %2 %1))

;; ## Math helpers

(defn round [num] (.round js/Math num))

;; ## Development helpers

(defn debug [message] (.alert api message 10))

;; ## Grid functions

(defn calculate-grid
  ([coords]
     (calculate-grid (focused-window) coords))
  ([win {:keys [x y width height]}]
     (let [screen (.. win screen frameWithoutDockOrMenu)]
       (clj->js {:x (round (+ (* x (.-width screen)) (.-x screen)))
                 :y (round (+ (* y (.-height screen)) (.-y screen)))
                 :width (round (* width (.-width screen)))
                 :height (round (* height (.-height screen)))}))))

(defn size-to-grid [coords]
  (let [win (focused-window)]
    (.setFrame win (calculate-grid win coords))))

;; ## Layouts


;; ## Movement functions

(defn push-left []
  (size-to-grid {:x 0
                 :y 0
                 :width 0.5
                 :height 1}))

(defn push-right []
  (size-to-grid {:x 0.5
                 :y 0
                 :width 0.5
                 :height 1}))

(defn center-window []
  (size-to-grid {:x 0.25
                 :y 0
                 :width 0.5
                 :height 1}))

(defn to-full-screen []
  (size-to-grid {:x 0
                 :y 0
                 :width 1
                 :height 1}))

(defn upper-left []
  (size-to-grid {:x 0
                 :y 0
                 :width 0.5
                 :height 0.5}))

(defn upper-right []
  (size-to-grid {:x 0.5
                 :y 0
                 :width 0.5
                 :height 0.5}))

(defn lower-left []
  (size-to-grid {:x 0
                 :y 0.5
                 :width 0.5
                 :height 0.5}))

(defn lower-right []
  (size-to-grid {:x 0.5
                 :y 0.5
                 :width 0.5
                 :height 0.5}))

;; ## Window focus operations

(defn visible-windows []
  (.visibleWindowsMostRecentFirst Window))

(defn app-for-win [win]
  (.app win))

(defn hide [win]
  (.hide (app-for-win win)))

(defn hide-all []
  (_map hide (visible-windows)))

;; ## Application launching

;; ## Keybindings

(def empty-mods (js/Array.))
(def mash (js/Array. "ctrl" "alt" "cmd"))
(def super (js/Array. "ctrl" "cmd"))
(def super-meta (js/Array. "alt" "cmd"))

(defn bind [letter chord fn]
  (.bind api letter chord fn))

(bind "left" mash push-left)
(bind "right" mash push-right)

;; # Capewell bindings
(bind "t" super upper-left)
(bind "n" super upper-right)
(bind "p" super lower-left)
(bind "l" super lower-right)

(bind "c" mash center-window)
(bind "m" mash to-full-screen)
(bind "h" mash hide-all)

