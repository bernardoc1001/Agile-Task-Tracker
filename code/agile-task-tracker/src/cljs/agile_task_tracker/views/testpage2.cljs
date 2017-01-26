(ns agile-task-tracker.views.testpage2
  (:require [reagent.core :as r]))

(defn testpage2-page []
  [:div
   [:p " shiney This is the 2nd pagey"]
   [:p [:a {:href "#/"} "To dashboard"]]])
