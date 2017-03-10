(ns agile-task-tracker.views.current-sprint
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.task-ajax :as task-ajax]
            [agile-task-tracker.sortable :as sortable]
            [reagent.session :as session]))

(defn load-tasks []
  (task-ajax/query-active-sprint-tasks (session/get :project-id)))

(defn refresh-current-tasks-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(task-ajax/query-active-sprint-tasks (session/get :project-id))}
   "Refresh Tasks"])

(defn current-sprint-page []
  [:div
   [:div#wrapper
    [sidebar/sidebar]
    [:div {:class "jumbotron"}
     [:h2 "Current Sprint"]]
    [:div.page-content-wrapper>div.container>div.row>div.col-lg-12
     [:div {:class "row"}
      [:div {:class "col-sm-4"}
       [:div {:class "panel panel-default"}
        [:div {:class "panel-heading"} "To Do"]
        [:div {:class "panel-body"}
         [:div {:class "panel panel-default"}
          [:div {:class "panel-body"}
           [:div
            [rmodals/modal-window]
            [refresh-current-tasks-button]]
           [:div
            [:div.column {:id "to-do-col"}]]]]]]]

      [:div {:class "col-sm-4"}
       [:div {:class "panel panel-default"}
        [:div {:class "panel-heading"} "In Progress"]
        [:div {:class "panel-body"}
         [:div {:class "panel panel-default"}
          [:div {:class "panel-body"}
           [:div
            [:div.column {:id "in-progress-col"}]]]]]]]

      [:div {:class "col-sm-4"}
       [:div {:class "panel panel-default"}
        [:div {:class "panel-heading"} "Completed"]
        [:div {:class "panel-body"}
         [:div {:class "panel panel-default"}
          [:div {:class "panel-body"}
           [:div
            [:div.column {:id "completed-col"}]]]]]]]]]]])

(defn current-sprint-did-mount []
  (load-tasks)
  (sortable/sortable-column))

(defn current-sprint []
  (r/create-class {:reagent-render      current-sprint-page
                   :component-did-mount current-sprint-did-mount}))
