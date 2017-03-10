(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [goog.string :as gstring]
            [agile-task-tracker.sidebar :as sidebar]
            [ajax.core :refer [GET POST]]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
            [agile-task-tracker.task-ajax :as task-ajax]
            [agile-task-tracker.sprint-ajax :as sprint-ajax]
            [agile-task-tracker.deactivate-sprint :as deactivate-sprint]
            [agile-task-tracker.sortable :as sortable]
            [reagent.session :as session]
            [clojure.string :as string]))

;TODO rename atoms. This isn't turning yellow. THE SKY IS FALLING. Wait
; nevermind there we go
(defonce new-task
         (r/atom {:data {}}))

(defonce new-sprint
         (r/atom {:sprint {}}))

(defonce current-sprint-id
         (r/atom {:sprint-id ""}))

(defn load-tasks []
  (task-ajax/query-tasks-by-sprint-id "backlog")
  (task-ajax/query-active-sprint-tasks (session/get :project-id)))

(defn refresh-backlog-tasks-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(load-tasks)}
   "Refresh Tasks"])

(defn task-id-contains-white-space
  [task-map]
  (boolean (re-find #" " (:task-id task-map))))

(defn validate-task
  "Checks task for required info, returns true if correct."
  [task-map]
  (let  [tid-blank? (string/blank? (:task-id task-map))
         name-blank? (string/blank? (:task-title task-map))
         creator-blank? (string/blank? (:created-by task-map))]
    (and (not tid-blank?) (not name-blank?) (not creator-blank?))))


(defn save-task-procedure
  "Posts task info if true, alerts user if false"
  ;TODO deactivate old sprints when creating new sprint
  [task-map]
  (if (validate-task task-map)
    (if (task-id-contains-white-space task-map)
      (js/alert "Please remove whitespace from id")
      (task-ajax/put-task-by-id task-map))
    (js/alert "Please fill out required details")))


;TODO validate times.
(defn modal-task-creation-content []
  (swap! new-task assoc-in [:data :sprint-id] "backlog")
  (swap! new-task assoc-in [:data :priority-level] "Low")
  (swap! new-task assoc-in [:data :project-id] (session/get :project-id))
  (swap! new-task assoc-in [:data :logged-time] 0)

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
               :placeholder "Enter Creator" :on-change #(common/onclick-swap-atom! new-task [:data :created-by]%)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "assignees"} "Assignees: "]
      [:input {:type "text", :class "form-control", :id "Assignees",
               :placeholder "Whoever is assigned to this task" :on-change #(common/onclick-swap-atom! new-task [:data :assignees]%)}]]

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
               :placeholder "Enter Epic of task" :on-change #(common/onclick-swap-atom! new-task [:data :epic]%)}]]]]


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


;-----------------------------------------------------------------------------------------------------
(defn update-unassociated-task
  [string-map]
  (hash-map :task-id (:task-id string-map)
            :task-title (:task-title string-map)
            :description (:description string-map)
            :created-by (:created-by string-map)
            :assignees (:assignees string-map)
            :estimated-time (js/parseFloat (:estimated-time string-map))
            :epic (:epic string-map)
            :sprint-id (:sprint-id @current-sprint-id)      ;update the sprint-id here
            :priority-level (:priority-level string-map)
            :task-state "to-do-col"                         ;update the task-state here
            :logged-time (js/parseFloat (:logged-time string-map))
            :project-id (:project-id string-map)))

(defn associate-tasks-with-sprint-handler
  [response]
  (.log js/console (str "associate-tasks-with-sprint-handler response: " response))
  (let [hits-vector (get-in response [:hits :hits])]
    (if (not (empty? hits-vector))
      (doseq [hit hits-vector]
        (task-ajax/put-task-by-id (update-unassociated-task (:_source hit)))))))

(defn associate-tasks-with-sprint
  [sprint-map]
  (swap! current-sprint-id assoc :sprint-id (:sprint-id sprint-map))
  (POST (route-calculator)
        {:params        {:data   {:project-id (session/get :project-id)}
                         :method "get-unassigned-tasks"}
         :handler associate-tasks-with-sprint-handler
         :error-handler error-handler}))

(defn validate-sprint
  "Checks sprint for required info, returns true if correct."
  [sprint-map]
  (let  [sid-blank? (string/blank? (:sprint-id sprint-map))
         name-blank? (string/blank? (:sprint-name sprint-map))
         proj-blank? (string/blank? (:project-id sprint-map))]
    (and (not sid-blank?) (not name-blank?) (not proj-blank?))))

(defn sprint-id-contains-white-space [sprint-map]
  (boolean (re-find #" " (:sprint-id sprint-map))))

(defn save-sprint-procedure
  "Posts sprint info if true, alerts user if false"
  ;TODO deactivate old sprints when creating new sprint
  [sprint-map]
  (if (validate-sprint sprint-map)
    (if (sprint-id-contains-white-space sprint-map)
      (js/alert "Please remove whitespace from id")

      (do
        (sprint-ajax/put-sprint-by-id sprint-map)
        (associate-tasks-with-sprint sprint-map)
        (js/alert "Sprint Created")))
    (js/alert "Please fill out required details")))

(defn sprint-creation-content []
  (swap! new-sprint assoc-in [:data :project-id] (session/get :project-id))
  (swap! new-sprint assoc-in [:data :sprint-state] "active")
  [:div

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
     [:input {:type "date", :class "form-control", :id "end-date" :on-change #(common/onclick-swap-atom! new-sprint [:data :end-date] %)}]]]



   [:div.btn.btn-primary {:type         "button"
                          :data-dismiss "modal"
                          :on-click     #(save-sprint-procedure (:data @new-sprint))}
    "Save"]

   [:div.btn.btn-primary {:type         "button"
                          :data-dismiss "modal"
                          :on-click     #(deactivate-sprint/deactivate-sprints (session/get :project-id))}
    "End Sprint"]])

;;-------------------------------------------------------------------------------------------------------------------

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
                            :on-click     #(task-ajax/query-tasks-by-sprint-id (get-in @new-task [:data :sprint-id]))}
      "Query"]]]])

(defn query-tasks-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-query-tasks-by-sprint]
                               {:show (reset! new-task {})})}
   "Query By Sprint"])


;--------------------Backlog Page----------------------------------------------------------
(defn backlog-page []
  (load-tasks)
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

            [get-task-button]
            [query-tasks-button]
            ;---------------------------------------------------------
            [refresh-backlog-tasks-button]

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
             [sprint-creation-content]]]]]]]]]]



     [sidebar/menu-toggle]
     ;TODO ask Renaat about debug info
     [:p (str "new-task: " @new-task)]
     [:p (str "new-sprint: " @new-sprint)]]])



(defn backlog-did-mount []
  (sortable/sortable-column))

(defn backlog []
  (r/create-class {:reagent-render      backlog-page
                   :component-did-mount backlog-did-mount}))
