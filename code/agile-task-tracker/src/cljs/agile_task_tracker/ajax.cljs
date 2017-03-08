(ns agile-task-tracker.ajax
  (:require [reagent.session :as session]))

(defn handler
  [response]
  (.log js/console (str "handler response: " response)))

(defn error-handler
  [response]
  (.error js/console (str response)))


(defn route-calculator
  "Returns a string representing the route of an ajax method"
  []
  (let [current-page-name (session/get :current-page-name)]
    (cond
      (= current-page-name "backlog-page")
      (str "/backlog")

      (= current-page-name "dashboard-page")
      (str "/")

      (= current-page-name "project-page")
      (str "/project")

      (= current-page-name "sprints-page")
      (str "/sprints"))))