;
; Toby - A YouTube player for the desktop
;
; Frank Hale <frankhale@gmail.com>
; 8 July 2015
;
; License: GNU GPL v2
;
(ns main.core)

(def app (js/require "app"))
(def path (js/require "path"))
(def browser-window (js/require "browser-window"))
(def global-shortcut (js/require "global-shortcut"))
(def crash-reporter (js/require "crash-reporter"))
(def process (js/require "process"))

(def main-window (atom nil))
(def assets-dir (apply str (interpose path.sep [(.cwd process) "resources" "app" "assets"])))
(js/console.log assets-dir)
(def browser-options (clj->js {
  :title "Toby - A YouTube player for the desktop"
  :width 640
  :height 400
  :icon (str assets-dir path.sep "images" path.sep "t.png")
  :resizable true}))

(defn toggle-dev-tools []
  (when-let [focused-win (.getFocusedWindow browser-window)]
    (.toggleDevTools focused-win)))

(defn reload []
  (when-let [focused-win (.getFocusedWindow browser-window)]
    (.reloadIgnoringCache focused-win)))

(defn init-browser []
	(reset! main-window (browser-window. browser-options))
  (.register global-shortcut (if (= js/process.platform "darwin") "Cmd+Alt+I" "Ctrl+Shift+I") #(toggle-dev-tools))
  (.register global-shortcut "CmdOrCtrl+R" #(reload))
	;(.openDevTools @main-window)
  (.loadUrl @main-window (str "file://" assets-dir "\\html\\toby.html"))
	(.on @main-window "closed" #(reset! main-window nil)))

(defn quit []
  (js/console.log "app.quit called")
  (.quit app))

(.start crash-reporter)
(.on app "window-all-closed" #(when-not (= js/process.platform "darwin") (quit)))
(.on app "ready" #(init-browser))