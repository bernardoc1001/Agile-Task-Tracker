(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [goog.string :as gstring]
						[agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.task-ajax :as task-ajax]
            [agile-task-tracker.sprint-ajax :as sprint-ajax]
            [agile-task-tracker.sortable :as sortable]))

;TODO rename atoms. This isn't turning yellow. THE SKY IS FALLING. Wait
; nevermind there we go
(defonce new-task
         (r/atom {:data {}}))

(defonce new-sprint
         (r/atom {:sprint {}}))

(defn refresh-tasks-button []
  [:div.btn.btn-primary.btn-backlog-col
   ;TODO query both backlog tasks and the active sprint
   {:on-click #(task-ajax/query-tasks-by-sprint "backlog")}
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
    [:div [common/atom-input-field "Project-id: " new-task [:data :proj-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(task-ajax/put-task-by-id (:data @new-task))}
      "Save"]]]])

(defn create-task-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-task-creation-content]
                               {:show (reset! new-task {})})}
   "Create Task"])


(defn modal-sprint-creation-content []
  [:div
   [:div {:class "modal-header"}
    [:button {:type "button"
              :class "close"
              :data-dismiss "modal"
              :aria-label "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id "sprint-modal-title"}
     "Create a sprint"]]
   [:div {:class "modal-body"}
    ;TODO auto-gen removal. Sprint title or human friendly id to differ
    ; between them
    [:div [common/atom-input-field "sprint ID: " new-sprint [:data :sprint-id]]]
    [:div [common/atom-input-field "Start-Date: " new-sprint [:data :start-date]]]
    [:div [common/atom-input-field "End-Date: " new-sprint [:data :end-date]]]
    [:div [common/atom-input-field "Project ID: " new-sprint [:data :proj-id]]]
    [:div [common/atom-input-field "Sprint State: " new-sprint [:data :sprint-state]]]


    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(sprint-ajax/put-sprint-by-id  (:data @new-sprint))}
      "Save"]]]])

(defn create-sprint-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-sprint-creation-content]
                               {:show (reset! new-sprint {})})}
   "Create Sprint"])

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
                            :on-click     #(task-ajax/get-task-by-id (get-in @new-task [:data :task-id]))}
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
          :id "delete-task-modal-title"}
     "Delete a task"]]
   [:div {:class "modal-body"}

    [:div [common/atom-input-field "Task ID: " new-task [:data :task-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(task-ajax/delete-task-by-id (:data @new-task))}
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
          :id "query-modal-title"}
     "Query A Sprint"]]
   [:div {:class "modal-body"}

    [:div [common/atom-input-field "Sprint ID: " new-task [:data :sprint-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(task-ajax/query-tasks-by-sprint (get-in @new-task [:data :sprint-id]))}
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
            ]]]

         [:div {:class "col-sm-6"}
          [:div {:class "panel panel-default"}
           [:div {:class "panel-body"}
            [:div
             [create-sprint-button]]]]]]]]]
     
     [sidebar/menu-toggle]
     ;TODO ask Renaat about debug info
     [:p (str "new-task: " @new-task)]
     [:p (str "new-sprint: " @new-sprint)]]]])



(defn backlog-did-mount []
  (sortable/sortable-column))

(defn backlog []
  (r/create-class {:reagent-render      backlog-page
                   :component-did-mount backlog-did-mount}))
