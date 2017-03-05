(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [ajax.core :refer [GET POST]]
            [goog.string :as gstring]
						[agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.draggable-tasks :as task-portlet]
            [hipo.core :as hipo]))

(defonce page-state
         (r/atom {:tasks []}))

(defonce new-task
         (r/atom {:data {}}))

(defn convert-to-task-format
  [string-map]
  (hash-map :task-id (:task-id string-map)
            :task-title (:task-title string-map)
            :description (:description string-map)
            :created-by (:created-by string-map)
            :assignees (:assignees string-map)
            :estimated-time (js/parseFloat (:estimated-time string-map))
            :epic (:epic string-map)
            :sprint-id (:sprint-id string-map)
            :priority-level (js/parseInt (:priority-level string-map))
            :task-state (:task-state string-map)
            :logged-time (js/parseFloat (:logged-time string-map))
            :project-id (:project-id string-map)))

(defn render-task
  [task-map col-id]
  (task-portlet/delete-task-portlet (:task-id task-map))
  (if (nil? (.getElementById js/document (:task-id task-map)))
    (let [draggable-portlet (hipo/create (task-portlet/create-task-portlet task-map))]

      (.appendChild (.getElementById js/document col-id) draggable-portlet)
      (task-portlet/make-tasks-toggleable task-map))
    (.error js/console (str "Could not add task, old version of the task still exists"))))

;-----------test ajax stuff, refactor this -------------------------

(defn handler
  [response]
  (.log js/console (str "handler response: " response)))

(defn error-handler
  [response]
  (.error js/console (str response)))

(defn get-task-by-id-handler
  [response]
  (.log js/console (str "get-task-by-id-handler response: " response))
  (render-task (convert-to-task-format (get-in response [:_source])) "backlog-col"))

(defn get-task-by-id
  [task-id]
  (POST "/backlog"
        {:params        {:data   {:task-id task-id}
                         :method "get-by-id"}
         :handler       get-task-by-id-handler
         :error-handler error-handler}))

(defn put-task-by-id-handler
  [response]
  (.log js/console (str "put-task-handler response: " response))
  ;task will be rendered in the get response handler
  (get-task-by-id (:_id response)))

(defn delete-task-by-id-handler
  [response]
  (.log js/console (str "delete-task-by-id-handler response: " response))
  (task-portlet/delete-task-portlet (:_id response)))

(defn query-tasks-by-sprint-handler
  [response]
  (.log js/console (str "query-task-handler response: " response))
  (let [hits-vector (get-in response [:hits :hits])]
    (doseq [hit hits-vector]
      (render-task (convert-to-task-format (:_source hit)) "backlog-col"))))


;-------------------------------------------------------------------
(defn save-task-procedure []
  ;TODO make this single arity?
  (POST "/backlog"
        {:params        (:data @new-task)
         :handler       put-task-by-id-handler
         :error-handler error-handler}))


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
    [:div [common/atom-input-field "Priority Level: " "number" new-task [:data :priority-level]]]
    [:div [common/atom-input-field "Task State: " new-task [:data :task-state]]]
    [:div [common/atom-input-field "Logged Time: " "time" new-task [:data :logged-time]]]
    [:div [common/atom-input-field "Project-id: " new-task [:data :project-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(save-task-procedure)}
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
                            :on-click     #(get-task-by-id (get-in @new-task [:data :task-id]))}
      "Get Task"]]]])

(defn get-task-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-get-task-by-id]
                               {:show (reset! new-task {})})}
   "Get Task By ID"])
;--------------------------------------------------------------------------------------

;--------delete doc by id example------------------------------------------------------
(defn delete-task-by-id []
  (POST "/backlog"
        {:params        {:data (:data @new-task)
                         :method "delete-by-id"}
         :handler       delete-task-by-id-handler
         :error-handler error-handler}))

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
                            :on-click     #(delete-task-by-id)}
      "Delete Task"]]]])

(defn delete-task-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-delete-task-by-id]
                               {:show (reset! new-task {})})}
   "Delete Task By ID"])
;--------------------------------------------------------------------------------------
;--------------------query all tasks by assigned sprint example -----------------------
(defn query-tasks-by-sprint [sprint-id]
  (POST "/backlog"
        {:params        {:data {:sprint-id sprint-id}
                         :method "query-by-term"}
         :handler       query-tasks-by-sprint-handler
         :error-handler error-handler}))

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
                            :on-click     #(query-tasks-by-sprint (get-in @new-task [:data :sprint-id]))}
      "Query"]]]])

(defn query-tasks-button []
  [:div.btn.btn-primary.btn-backlog-col
   {:on-click #(rmodals/modal! [modal-query-tasks-by-sprint]
                               {:show (reset! new-task {})})}
   "Query By Sprint"])
;--------------------------------------------------------------------------------------
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
