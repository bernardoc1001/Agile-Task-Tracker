(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [goog.string :as gstring]
						[agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.task-portlet :as task-portlet]
            [agile-task-tracker.task-ajax :as task-ajax]))

(defonce page-state
         (r/atom {:tasks []}))

(defonce new-task
         (r/atom {:data {}}))

(defn refresh-tasks-button []
  [:div.btn.btn-primary.btn-backlog-col
   ;TODO query both backlog tasks and the active sprint
   {:on-click #(task-ajax/query-tasks-by-sprint "/backlog" "backlog")}
   "Refresh Tasks"])

(defn modal-task-creation-content []
  [:div
   [:div {:class "modal-header"}
    [:button {:type "button"
              :class "close"
              :data-dismiss "modal"
              :aria-label "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id "task-modal-title"}
     "Create a task"]]
   [:div {:class "modal-body"}

    [:div [common/atom-input-field "Task ID: " new-task [:data :task-id]]]
    [:div [common/atom-input-field "Title: " new-task [:data :task-title]]]
    [:div [common/atom-input-field "Description: " new-task [:data :description]]]
    [:div [common/atom-input-field "Created By: " new-task [:data :created-by]]]
    [:div [common/atom-input-field "Assignees: " new-task [:data :assignees]]]
    [:div [common/atom-input-field "Estimated Time: " "number" new-task [:data :estimated-time]]]
    [:div [common/atom-input-field "Epic: " new-task [:data :epic]]]
    [:div [common/atom-input-field "Sprint ID: " new-task [:data :sprint-id]]]
    [:div [common/atom-input-field "Priority Level: " new-task [:data :priority-level]]]
    [:div [common/atom-input-field "Task State: " new-task [:data :task-state]]]
    [:div [common/atom-input-field "Logged Time: " "time" new-task [:data :logged-time]]]
    [:div [common/atom-input-field "Project-id: " new-task [:data :project-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(task-ajax/put-task-by-id "/backlog" (:data @new-task))}
      "Save"]]]])

(defn create-task-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-task-creation-content]
                               {:show (reset! new-task {})})}
   "Create Task"])


;;----------------Get doc by ID example -------------------------------------------

(defn modal-get-task-by-id []
  [:div
   [:div {:class "modal-header"}
    [:button {:type "button"
              :class "close"
              :data-dismiss "modal"
              :aria-label "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id "get-task-modal-title"}
     "Get a task"]]
   [:div {:class "modal-body"}

     [:div [common/atom-input-field "Task ID: " new-task [:data :task-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(task-ajax/get-task-by-id "/backlog" (get-in @new-task [:data :task-id]))}
      "Get Task"]]]])

(defn get-task-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-get-task-by-id]
                               {:show (reset! new-task {})})}
   "Get Task By ID"])
;--------------------------------------------------------------------------------------

;--------delete doc by id example------------------------------------------------------


(defn modal-delete-task-by-id []
  [:div
   [:div {:class "modal-header"}
    [:button {:type "button"
              :class "close"
              :data-dismiss "modal"
              :aria-label "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id "get-task-modal-title"}
     "Delete a task"]]
   [:div {:class "modal-body"}

    [:div [common/atom-input-field "Task ID: " new-task [:data :task-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(task-ajax/delete-task-by-id "/backlog" (:data @new-task))}
      "Delete Task"]]]])

(defn delete-task-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-delete-task-by-id]
                               {:show (reset! new-task {})})}
   "Delete Task By ID"])
;--------------------------------------------------------------------------------------
;--------------------query all tasks by assigned sprint example -----------------------


(defn modal-query-tasks-by-sprint []
  [:div
   [:div {:class "modal-header"}
    [:button {:type "button"
              :class "close"
              :data-dismiss "modal"
              :aria-label "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id "get-task-modal-title"}
     "Query A Sprint"]]
   [:div {:class "modal-body"}

    [:div [common/atom-input-field "Sprint ID: " new-task [:data :sprint-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(task-ajax/query-tasks-by-sprint "/backlog" (get-in @new-task [:data :sprint-id]))}
      "Query"]]]])

(defn query-tasks-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-query-tasks-by-sprint]
                               {:show (reset! new-task {})})}
   "Query By Sprint"])


;--------------------Backlog Page----------------------------------------------------------
(defn backlog-page []
  [:div
   [:div#wrapper
    [sidebar/sidebar]

    [:div.page-content-wrapper>div.container>div.row>div.col-lg-12
     [sidebar/menu-toggle]
     [:p (str "page-state: " @page-state)]
     [:p (str "new-task: " @new-task)]
     [:p "backlog test"]

     [:div {:class "row"}
      [:div {:class "col-sm-4"}
       [:div {:class "panel panel-default"}
        [:div {:class "panel-heading"} "Backlog"]
        [:div {:class "panel-body"}
         [:div {:class "panel panel-default"}
          [:div {:class "panel-body"}
           [:div
            [rmodals/modal-window]
            [create-task-button]

            ;----------temporary examples-----------------------------
            [rmodals/modal-window]
            [get-task-button]

            [rmodals/modal-window]
            [delete-task-button]

            [rmodals/modal-window]
            [query-tasks-button]
            ;---------------------------------------------------------
            [refresh-tasks-button]

            ]
           ;portlet stuff
           [:div
            [:div.column {:id "backlog-col"}]]]]]]]


      [:div {:class "col-sm-8"}
       [:div {:class "panel panel-default"}
        [:div {:class "panel-heading"} "Sprint Creation/Modification"]
        [:div {:class "panel-body"}
         [:div {:class "col-sm-6"}
          [:div {:class "panel panel-default"}
           [:div {:class "panel-body"}
            ;portlet stuff sprint
            [:div.column {:id "create-sprint-col"}]
            [:div {:id "progressbar"} ]]]]

         [:div {:class "col-sm-6"}
          [:div {:class "panel panel-default"}
           [:div {:class "panel-body"}
            [:div [common/atom-input-field "Sprint Name " new-task [:sprint-name]]]]]]]]]]]]])


(defn backlog-did-mount []
  (js/$ (fn []
          (.sortable (js/$ ".column") (clj->js {:connectWith ".column"
                                                :handle ".portlet-header"
                                                :cancel ".portlet-toggle"
                                                :placeholder "portlet-placeholder ui-corner-all"})))))

(defn backlog []
  (r/create-class {:reagent-render      backlog-page
                   :component-did-mount backlog-did-mount}))
