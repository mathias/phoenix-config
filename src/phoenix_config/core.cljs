(ns phoenix-config.core)

;; Phoenix Globals

(def Window js/Window)
(def api js/api)

;; Math helpers
(defn round [num] (.round js/Math num))
(defn half [num] (/ num 2))

(defn pushLeft []
  (let [win (.focusedWindow Window)
        frame (.frame win)
        screen (.. win screen frameWithoutDockOrMenu)]
    (aset frame "x" (.-x screen))
    (aset frame "y" (.-y screen))

    (aset frame "width" (round (half (.-width screen))))
    (aset frame "height" (.-height screen))

    (.setFrame win frame)))

(defn fullscreen []
  (let [win (.focusedWindow Window)
        screen (.. win screen frameWithoutDockOrMenu)]
    (.setFrame win screen)))

(def mash (js/Array. "ctrl" "alt" "cmd"))

(.bind api "left" mash pushLeft)
(.bind api "m" mash fullscreen)
