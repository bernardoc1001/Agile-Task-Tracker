(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
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

;-----------test ajax stuff, refactor this -------------------------
(defn handler
  [response]
  (.log js/console (str "handler response: " response)))

(defn put-task-by-id-handler
  [response]
  (.log js/console (str "put-task-handler response: " response))
  (let [draggable-portlet (hipo/create (task-portlet/create-task-portlet (get-in @new-task [:data])))]
    (swap! page-state assoc-in [:tasks] (conj (:tasks @page-state) (:data @new-task)))
    (.appendChild (.getElementById js/document "backlog-col") draggable-portlet)
    (task-portlet/make-tasks-draggable)))

(defn get-task-by-id-handler
  [response]
  ;TODO convert relevant strings to their respective number types
  (println (str "access the id: " (get-in response [:_source :task-id])))
  (println (str "access the title: " (get-in response [:_source :task-title])))
  (.log js/console (str "get-task-by-id-handler response: " response)))

(defn error-handler
  [response]
  #_(.log js/console (str "errorhandler- something bad happened: " status " " status-text))
  (.error js/console (str response)))
;-------------------------------------------------------------------
(defn save-task-procedure []
  (POST "/backlog"
        {:params        (:data @new-task)
         :handler       put-task-by-id-handler
         :error-handler error-handler}))

(defn atom-input-field
  ([label type atom path]
   (if (and (= type "number") (not= (first path) :priority-level))
     ;take in times as a number accurate to 3 decimal places
     [:label label [:input {:type      "number"
                            :step      "0.001"
                            :name      label
                            :on-change #(common/onclick-swap-atom! atom path %)}]]

     ;take in every other type (including non-time numbers , i.e priority-level)
     [:label label [:input {:type      type
                            :name      label
                            :on-change #(common/onclick-swap-atom! atom path %)}]]))
  ([label atom path]
    (atom-input-field label "text" atom path)))

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

    [:div [atom-input-field "Task ID: " new-task [:data :task-id]]]
    [:div [atom-input-field "Title: " new-task [:data :task-title]]]
    [:div [atom-input-field "Description: " new-task [:data :description]]]
    [:div [atom-input-field "Created By: " new-task [:data :created-by]]]
    [:div [atom-input-field "Assignees: " new-task [:data :assignees]]]
    [:div [atom-input-field "Original Estimate: " "number" new-task [:data :original-estimate]]]
    [:div [atom-input-field "Remaining Estimate: " "number" new-task [:data :remaining-estimate]]]
    [:div [atom-input-field "Epic: " new-task [:data :epic]]]
    [:div [atom-input-field "Assigned Sprint: " new-task [:data :assigned-sprint]]]
    [:div [atom-input-field "Priority Level: " "number" new-task [:data :priority-level]]]
    [:div [atom-input-field "Task State: " new-task [:data :task-state]]]
    [:div [atom-input-field "Logged Time: " "number" new-task [:data :logged-time]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(save-task-procedure)}
      "Save"]]]])

(defn create-task-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [modal-task-creation-content]
                               {:show (reset! new-task {})})}
   "Create Task"])


;;----------------Get doc by ID example -------------------------------------------



(defn get-task-by-id []
  (POST "/backlog"
        {:params        {:data (:data @new-task)
                         :method "get-by-id"}
         :handler       get-task-by-id-handler
         :error-handler error-handler}))

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

    [:div [atom-input-field "Task ID: " new-task [:data :task-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(get-task-by-id)}
      "Get Task"]]]])

(defn get-task-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [modal-get-task-by-id]
                               {:show (reset! new-task {})})}
   "Get Task By ID"])
;--------------------------------------------------------------------------------------

;--------delete doc by id example------------------------------------------------------
(defn delete-task-by-id []
  (POST "/backlog"
        {:params        {:data (:data @new-task)
                         :method "delete-by-id"}
         :handler       handler
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

    [:div [atom-input-field "Task ID: " new-task [:data :task-id]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(delete-task-by-id)}
      "Delete Task"]]]])

(defn delete-task-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [modal-delete-task-by-id]
                               {:show (reset! new-task {})})}
   "Delete Task By ID"])
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
                 ;---------------------------------------------------------
                 ]
								;portlet stuff
								[:div
								 [:div.column {:id "backlog-col"}
                  #_(task-portlet/create-task-portlet {:task-id "1"
                                                     :task-title "2"
                                                     :description "Yep"})]]]]]]]


					 [:div {:class "col-sm-8"}
						[:div {:class "panel panel-default"}
						 [:div {:class "panel-heading"} "Sprint Creation/Modification"]
						 [:div {:class "panel-body"}
							[:div {:class "col-sm-6"}
							 [:div {:class "panel panel-default"}
								[:div {:class "panel-body"}
								 ;portlet stuff sprint
								 [:div.column {:id "create-sprint-col"}]]]]

							[:div {:class "col-sm-6"}
							 [:div {:class "panel panel-default"}
								[:div {:class "panel-body"}
								 [:div [atom-input-field "Sprint Name " new-task [:sprint-name]]]]]]]]]]]]])

(defn backlog-did-mount []
  (js/$ (fn []
          (.sortable (js/$ ".column") (clj->js {:connectWith ".column"
                                                :handle ".portlet-header"
                                                :cancel ".portlet-toggle"
                                                :placeholder "portlet-placeholder ui-corner-all"})))))


(defn backlog []
  (r/create-class {:reagent-render      backlog-page
                   :component-did-mount backlog-did-mount}))
