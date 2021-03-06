(ns agile-task-tracker.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [agile-task-tracker.views.dashboard :refer [dashboard]]
              [agile-task-tracker.views.backlog :refer [backlog]]
							[agile-task-tracker.views.project :refer [project]]
              [agile-task-tracker.views.current-sprint :refer [current-sprint]]))


(defn current-page []
  [:div [(session/get :current-page)]])

(secretary/defroute "/" []
                    (session/put! :current-page #'dashboard)
                    (session/put! :current-page-name "dashboard-page"))

(secretary/defroute "/project/:organisation-id" [organisation-id]
                    (session/put! :organisation-id organisation-id)
                    (session/put! :current-page #'project)
                    (session/put! :current-page-name "project-page"))

(secretary/defroute "/backlog/:project-id" [project-id]
                    (session/put! :project-id project-id)
                    (session/put! :current-page #'backlog)
                    (session/put! :current-page-name "backlog-page"))

(secretary/defroute "/current-sprint/:project-id" [project-id]
                    (session/put! :project-id project-id)
                    (session/put! :current-page #'current-sprint)
                    (session/put! :current-page-name "current-sprint-page"))

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
