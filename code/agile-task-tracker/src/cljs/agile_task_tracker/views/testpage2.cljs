(ns agile-task-tracker.views.testpage2
  (:require [reagent.core :as r]))

(defn testpage2-page []
  [:div [:div
         [:p " shiney This is the 2nd pagey"]
         [:p [:a {:href "#/"} "To dashboard"]]]
   [:div
    [:ol [:li "List item 1"]
     [:li "List item 2"]]]])
