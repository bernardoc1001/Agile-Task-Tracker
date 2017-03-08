(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [goog.string :as gstring]
            [agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.task-ajax :as task-ajax]
            [agile-task-tracker.sprint-ajax :as sprint-ajax]
            [agile-task-tracker.sortable :as sortable]
            [reagent.session :as session]
            [clojure.string :as string]))

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

(defn validate-task
  "Checks task for required info, returns true if correct."
  [task-map]
  (let  [tid-blank? (string/blank? (:task-id task-map))
         name-blank? (string/blank? (:task-title task-map))
         assign-blank? (string/blank? (:assignees task-map))]
    (and (not tid-blank?) (not name-blank?) (not assign-blank?))))


(defn save-task-procedure
  "Posts task info if true, alerts user if false"
  ;TODO deactivate old sprints when creating new sprint
  [task-map]
  (if (validate-task task-map)
    (task-ajax/put-task-by-id  task-map)
    (js/alert "Please fill out required details")))


;TODO validate times.
(defn modal-task-creation-content []
  (swap! new-task assoc-in [:data :sprint-id] "backlog")
  (swap! new-task assoc-in [:data :priority-level] "Low")
  (swap! new-task assoc-in [:data :project-id] (session/get :project-id))

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
    ;TODO remove white space from id
    [:form
     [:div {:class "form-group"}
      [:label {:for "task-id-form"} "Task ID: "]
      [:input {:type "text", :class "form-control", :id "task-id-form",
               :placeholder "Enter Task ID" :on-change #(common/onclick-swap-atom! new-task [:data :task-id]%)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "task-tile"} "Task Title: "]
      [:input {:type "text", :class "form-control", :id "task-title",
               :placeholder "Enter Task Title" :on-change #(common/onclick-swap-atom! new-task [:data :task-title]%)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "description"} "Description "]
      [:input {:type "text", :class "form-control", :id "description",
               :placeholder "Enter Description" :on-change #(common/onclick-swap-atom! new-task [:data :description]%)}]]

     [:div {:class "form-group"}
      [:label {:for "created-by"} "Created by: "]
      [:input {:type "text", :class "form-control", :id "Created-by",
               :placeholder "Enter Creator" :on-change #(common/onclick-swap-atom! new-task [:data :created-by]%)}]]

     [:div {:class "form-group"}
      [:label {:for "assignees"} "Assignees: "]
      [:input {:type "text", :class "form-control", :id "Assignees",
               :placeholder "Whoever is assigned to this task" :on-change #(common/onclick-swap-atom! new-task [:data :assignees]%)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "estimated-time"} "Estimated time: "]
      [:input {:type "text", :class "form-control", :id "estimated-time",
               :placeholder "Enter number of hours required" :on-change #(common/onclick-swap-atom! new-task [:data :estimated-time]%)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "priority-level"} "Priority Level:"]
      [:select {:class "form-control" :id "priority" :defaultValue "Low" :on-change #(common/onclick-swap-atom! new-task [:data :priority-level]%)}
       [:option {:value "Low"} "Low"]
       [:option {:value "Medium"} "Medium"]
       [:option {:value "High"} "High"]]]

     [:div {:class "form-group"}
      [:label {:for "epic"} "Epic: "]
      [:input {:type "text", :class "form-control", :id "epic",
               :placeholder "Enter Epic of task" :on-change #(common/onclick-swap-atom! new-task [:data :epic]%)}]]


     [:div {:class "form-group"}
      [:label {:for "logged-time"} "Logged-time: "]
      [:input {:type "text", :class "form-control", :id "Logged-time",
               :placeholder "Enter logged time on task" :on-change #(common/onclick-swap-atom! new-task [:data :logged-time]%)}]]]]


    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"

                            :on-click     #(save-task-procedure (:data @new-task))}
      "Save"]]])


(defn create-task-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-task-creation-content]
                               {:show (reset! new-task {})})}
   "Create Task"])

(defn validate-sprint
  "Checks sprint for required info, returns true if correct."
  [sprint-map]
  (let  [sid-blank? (string/blank? (:sprint-id sprint-map))
         name-blank? (string/blank? (:sprint-name sprint-map))
         proj-blank? (string/blank? (:project-id sprint-map))]
    (and (not sid-blank?) (not name-blank?) (not proj-blank?))))


(defn save-sprint-procedure
  "Posts sprint info if true, alerts user if false"
  ;TODO deactivate old sprints when creating new sprint
  [sprint-map]
  (if (validate-sprint sprint-map)
    (sprint-ajax/put-sprint-by-id sprint-map)
    (js/alert "Please fill out required details")))

(defn modal-sprint-creation-content []
  (swap! new-sprint assoc-in [:data :project-id] (session/get :project-id))
  (swap! new-sprint assoc-in [:data :sprint-state] "active")
  [:div
   [:div {:class "modal-header"}
    [:button {:type         "button"
              :class        "close"
              :data-dismiss "modal"
              :aria-label   "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id    "sprint-modal-title"}
     "Create a sprint"]]
   [:div {:class "modal-body"}
    ;TODO auto-gen removal. Sprint title or human friendly id to differ
    ; between them
    [:form
     [:div {:class "form-group"}
      [:label {:for "sprint-id"} "Sprint ID: "]
      [:input {:type        "text", :class "form-control", :id "sprint-id",
               :placeholder "Enter Sprint ID" :on-change #(common/onclick-swap-atom! new-sprint [:data :sprint-id] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "sprint-name"} "Sprint Name: "]
      [:input {:type        "text", :class "form-control", :id "sprint-name",
               :placeholder "Name your Sprint" :on-change #(common/onclick-swap-atom! new-sprint [:data :sprint-name] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "start-date"} "Sprint Start-Date: "]
      [:input {:type "date", :class "form-control", :id "start-date" :on-change #(common/onclick-swap-atom! new-sprint [:data :start-date] %)}]]

     [:div {:class "form-group"}
      [:label {:for "end-date"} "Sprint End-Date: "]
      [:input {:type "date", :class "form-control", :id "end-date" :on-change #(common/onclick-swap-atom! new-sprint [:data :end-date] %)}]]]]


   [:div {:class "modal-footer"}
    [:div.btn.btn-secondary {:type         "button"
                             :data-dismiss "modal"}
     "Close"]
    [:div.btn.btn-primary {:type         "button"
                           :data-dismiss "modal"
                           :on-click     #(save-sprint-procedure (:data @new-sprint))}
     "Save"]]])

(defn create-sprint-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-sprint-creation-content]
                               {:show (reset! new-sprint {})})}
   "Create Sprint"])


;;----------------Get doc by ID example -------------------------------------------

(defn modal-get-task-by-id []
  [:div
   [:div {:class "modal-header"}
    [:button {:type         "button"
              :class        "close"
              :data-dismiss "modal"
              :aria-label   "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id    "get-task-modal-title"}
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

            ;[rmodals/modal-window]
            ;[delete-task-button]

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
