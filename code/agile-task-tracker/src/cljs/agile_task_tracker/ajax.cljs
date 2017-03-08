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
  (let [current-page (session/get :current-page)
        dashboard-page #'agile-task-tracker.views.dashboard/dashboard-page
        project-page #'agile-task-tracker.views.project/project-page
        sprints-page #'agile-task-tracker.views.sprints/sprints-page
        backlog-page #'agile-task-tracker.views.backlog/backlog]

    (cond
      (= current-page backlog-page)
      (str "/backlog")

      (= current-page dashboard-page)
      (str "/")

      (= current-page project-page)
      (str "/project")

      (= current-page sprints-page)
      (str "/sprints"))))