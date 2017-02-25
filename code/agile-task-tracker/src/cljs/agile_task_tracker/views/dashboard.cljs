(ns agile-task-tracker.views.dashboard
  (:require [reagent.core :as r]))

(defn dashboard-page []
  [:div
   [:p "woozers testy test test hi hi"]
   [:p [:a {:href "/backlog"} "Go to backlog"]]])