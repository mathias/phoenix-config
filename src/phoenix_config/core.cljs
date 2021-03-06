;; This is a self-documenting configuration in ClojureScript for
;; [Phoenix.app](https://github.com/sdegutis/Phoenix), a lightweight
;; scriptable OSX window manager.
;;
;; Phoenix is configured with JS, but I prefer to write ClojureScript,
;; and so my config accomplishes many nice things in a small amount
;; of ClojureScript.
;;
;; ## Usage
;;
;; Install Phoenix.app: <https://github.com/jasonm23/phoenix#install>
;; Install boot: <https://github.com/tailrecursion/boot#unix>
;;
;; Compile bindings to JS once with:
;;
;; <pre><code>boot compile-bindings</code></pre>
;;
;; Or have boot watch and recompile on each change to `src/phoenix.cljs`:
;;
;; <pre><code>boot watch-bindings</code></pre>
;;
;; Then, symlink the configuration file into place:
;;
;; <pre><code>ln -s ~/path/to/phoenix_config/target/phoenix.js ~/.phoenix.js</code></pre>
;;
;; When Phoenix.app is run, it should pick up and use the config file. Et voila!
;;
;; ## Extra credit
;;
;; Installing <https://github.com/puffnfresh/toggle-osx-shadows> will make
;; layouts look a lot nicer!

(ns phoenix-config.core)

;; ## Phoenix Globals

(def App js/App)

(def Window js/Window)
(def visible-windows #(.visibleWindowsMostRecentFirst Window))
(def focused-window #(.focusedWindow Window))

(def api js/api)

;; ## Math helpers

(defn round [num] (.round js/Math num))

;; ## Development helpers

(defn debug [message] (.alert api message 5))
(defn log [message] (.log api message))

;; ## Grid functions

(defn calculate-grid
  [win {:keys [x y width height]}]
  (let [screen (.. win screen frameWithoutDockOrMenu)]
    (clj->js {:x (round (+ (* x (.-width screen)) (.-x screen)))
              :y (round (+ (* y (.-height screen)) (.-y screen)))
              :width (round (* width (.-width screen)))
              :height (round (* height (.-height screen)))})))

(defn size-to-grid
  ([coords]
     (size-to-grid (focused-window) coords))
  ([win coords]
     (.setFrame win (calculate-grid win coords))))

;; ## Layouts

;; ### Browser Layout
;;
;;     (1)              (2)              (3)              (4)
;; +-----+---+      +-----+---+      +-----+---+      +-----+---+
;; |     |   |      |     |   |      |     | 2 |      |     | 2 |
;; |     |   |      |     |   |      |     |   |      |     +---+
;; |  1  |   |  ->  |  1  | 2 |  ->  |  1  +---+  ->  |  1  | 3 |
;; |     |   |      |     |   |      |     | 3 |      |     +---+
;; |     |   |      |     |   |      |     |   |      |     | 4 |
;; +-----+---+      +-----+---+      +-----+---+      +-----+---+

(def browser-layout-rows (atom 2))

(defn browser-layout []
  [{:x 0
    :y 0
    :width 0.5
    :height 1}
   {:x 0.5
    :y 0
    :width 0.5
    :height (/ 1 @browser-layout-rows)}])

(defn layout-window-with-offset [coords y-offset rows]
  (merge coords {:y (/ (+ 0.0 y-offset) rows)
                 :height (/ 1 rows)}))

(defn browser-layout-positions []
  (let [browser-layout (browser-layout)
        num-vis-windows (count (visible-windows))
        right-col-windows (min (dec num-vis-windows) @browser-layout-rows)]
    (debug (str "Layout with " right-col-windows " right cols"))
    (into [(first browser-layout)]
          (map #(layout-window-with-offset (second browser-layout) % right-col-windows)
               (range right-col-windows)))))

(defn snap-all-to-layout []
  (let [layout-positions (browser-layout-positions)
        wins-to-snap (take (count layout-positions) (visible-windows))]
    (doall (map size-to-grid wins-to-snap layout-positions))))

(defn increase-browser-layout-rows []
  (swap! browser-layout-rows inc)
  (snap-all-to-layout)
  (debug (str "Right rows: " @browser-layout-rows)))

(defn decrease-browser-layout-rows []
  (swap! browser-layout-rows dec)
  (snap-all-to-layout)
  (debug (str "Right rows: " @browser-layout-rows)))

;; ### Hangout + Docs layout
;;
;; In this layout, the Docs window is on the right roughly the width of a normal
;; zoomed Google Docs document, and the Hangouts window fills up the space to
;; the left of it as big as possible.
;; Intended to be used on an Apple Cinema Display.

(defn snap-to-hangouts-layout []
  (let [layout-positions [{:x 0
                           :y 0
                           :width 0.70
                           :height 1}
                          {:x 0.70
                           :y 0
                           :width 0.3
                           :height 1}]
        wins-to-snap (take (count layout-positions) (visible-windows))]
    (doall (map size-to-grid wins-to-snap layout-positions))))


;; ## Movement functions

(defn push-left      [] (size-to-grid {:x 0    :y 0   :width 0.5 :height 1}))
(defn push-right     [] (size-to-grid {:x 0.5  :y 0   :width 0.5 :height 1}))
(defn center-window  [] (size-to-grid {:x 0.25 :y 0   :width 0.5 :height 1}))
(defn to-full-screen [] (size-to-grid {:x 0    :y 0   :width 1   :height 1}))
(defn upper-left     [] (size-to-grid {:x 0    :y 0   :width 0.5 :height 0.5}))
(defn upper-right    [] (size-to-grid {:x 0.5  :y 0   :width 0.5 :height 0.5}))
(defn lower-left     [] (size-to-grid {:x 0    :y 0.5 :width 0.5 :height 0.5}))
(defn lower-right    [] (size-to-grid {:x 0.5  :y 0.5 :width 0.5 :height 0.5}))

;; ## Window focus operations

(defn is-finder [app]
  (= "Finder" (.title app)))

(defn hide-all
  "OSX doesn't really let us hide all windows. But we can try to hide
  all of them by focusing Finder, hiding the rest, and minimizing
  Finder windows."
  []
  (let [apps (.runningApps js/App)
        finder-app (first (filter is-finder apps))
        other-apps (remove is-finder apps)]
    (.show finder-app)
    (doall (map #(.hide %) other-apps))
    (doall (map #(.minimize %) (.visibleWindows finder-app)))))

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
(bind "c" mash center-window)
(bind "m" mash to-full-screen)
;;(bind "h" mash hide-all)

;; ### vim-style bindings for HHKB
(bind "h" mash push-left)
(bind "l" mash push-right)

;; ### Capewell bindings

(bind "k" super push-left)
(bind "o" super push-right)
(bind "t" super upper-left)
(bind "i" super upper-right)
(bind "p" super lower-left)
(bind "u" super lower-right)

(bind "n" super center-window)
(bind "l" super to-full-screen)
(bind "h" super hide-all)

;; ### Layout bindings

(bind ";" super snap-all-to-layout)
(bind "H" super-meta snap-to-hangouts-layout) ;; Hangouts + Docs layout

;; ### Layout adjustment bindings

(bind "," super increase-browser-layout-rows)
(bind "'" super decrease-browser-layout-rows)
