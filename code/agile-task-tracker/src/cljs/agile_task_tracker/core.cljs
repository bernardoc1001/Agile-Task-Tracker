(ns agile-task-tracker.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [reagent.core :as r]
            [secretary.core :as secretary]
            [agile-task-tracker.views.dashboard :as dashboard]
            [agile-task-tracker.views.testpage2 :as testpage2]
            [agile-task-tracker.views.backlog :as backlog]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce debug?
         ^boolean js/goog.DEBUG)

(defonce app-state
         (r/atom {}))



;;;;;;;;;;;;;;
;;History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))


;;;;;;;;;;;;;;;
;; Routes

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
            (swap! app-state assoc :page :dashboard))

  (defroute "/testpage2" []
            (swap! app-state assoc :page :testpage2))
  (defroute "/backlog" []
            (swap! app-state assoc :page :backlog))

  (hook-browser-navigation!))


(defmulti current-page #(@app-state :page))
(defmethod current-page :dashboard []
  [dashboard/dashboard-page])
(defmethod current-page :testpage2 []
  [testpage2/testpage2-page])
(defmethod current-page :backlog []
  [backlog/backlog-page])
(defmethod current-page :default []
  [:div ])




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (r/render [current-page ]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (app-routes)
  (reload))