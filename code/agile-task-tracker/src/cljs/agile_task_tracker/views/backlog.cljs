(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [ajax.core :refer [GET POST]]
            [goog.string :as gstring]
						[agile-task-tracker.sidebar :as sidebar]))

(defonce page-state
         (r/atom {:tasks []}))

(defonce new-task
         (r/atom {:body {}}))
;-----------test ajax stuff, refactor this -------------------------
(defn handler
  [response]
  (println (str (empty? response)))
  (println (str (nil? response)))
  (.log js/console (str "handler response: " response)))

(defn error-handler
  [response]
  #_(.log js/console (str "errorhandler- something bad happened: " status " " status-text))
  (.error js/console (str response)))
;-------------------------------------------------------------------
(defn save-task-procedure
  [task]
  (let [updated-task-list (conj (:tasks @page-state) task)]
    (swap! page-state assoc-in [:tasks] updated-task-list)
    (println "new task:" (str (:body @new-task)))
    (POST "/backlog"
         {:params  (:body @new-task)
          :handler handler
          :error-handler error-handler})))

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

    [:div [atom-input-field "Task ID: " new-task [:body :task-id]]]
    [:div [atom-input-field "Title: " new-task [:body :task-title]]]
    [:div [atom-input-field "Description: " new-task [:body :description]]]
    [:div [atom-input-field "Created By: " new-task [:body :created-by]]]
    [:div [atom-input-field "Assignees: " new-task [:body :assignees]]]
    [:div [atom-input-field "Original Estimate: " "number" new-task [:body :original-estimate]]]
    [:div [atom-input-field "Remaining Estimate: " "number" new-task [:body :remaining-estimate]]]
    [:div [atom-input-field "Epic: " new-task [:body :epic]]]
    [:div [atom-input-field "Assigned Sprint: " new-task [:body :assigned-sprint]]]
    [:div [atom-input-field "Priority Level: " "number" new-task [:body :priority-level]]]
    [:div [atom-input-field "Task State: " new-task [:body :task-state]]]
    [:div [atom-input-field "Logged Time: " "number" new-task [:body :logged-time]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(save-task-procedure @new-task)}
      "Save"]]]])

(defn modal-window-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [modal-task-creation-content]
                               {:show (reset! new-task {})})}
   "Create Task"])


;;----------------Get doc by ID example -------------------------------------------
(defn get-task-by-id-handler
  [response]
  (println (str "get-task-by-id-handler response: " response)))

(defn handler2 [[ok response]]
  (if ok
    (.log js/console (str response))
    (.error js/console (str response))))

(defn get-task-by-id []
  (swap! new-task assoc-in [:body :lookup-task] true)
  (POST "/backlog"
        {:params  (:body @new-task)
         :handler handler
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

    [:div [atom-input-field "Task ID: " new-task [:body :task-id]]]

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
								 [modal-window-button]

                 [rmodals/modal-window]
                 [get-task-button]]
								;portlet stuff
								[:div
								 [:div.column
									[:div.portlet
									 [:div.portlet-header "Make Backlog page"]
									 [:div.portlet-content "Shaun should have this
                   finished already"]]]]]]]]]


					 [:div {:class "col-sm-8"}
						[:div {:class "panel panel-default"}
						 [:div {:class "panel-heading"} "Sprint Creation/Modification"]
						 [:div {:class "panel-body"}
							[:div {:class "col-sm-6"}
							 [:div {:class "panel panel-default"}
								[:div {:class "panel-body"}
								 ;portlet stuff sprint
								 [:div.column
									[:div.portlet
									 [:div.portlet-header "test"]
									 [:div.portlet-content "This is all the work Shaun has
                   done"]]]]]]

							[:div {:class "col-sm-6"}
							 [:div {:class "panel panel-default"}
								[:div {:class "panel-body"}
								 [:div [atom-input-field "Sprint Name " new-task [:sprint-name]]]]]]]]]

					 [:a {:href "/"} "Back to Homepage"]]]]])

(defn backlog-did-mount []
  (js/$ (fn []
          (.sortable (js/$ ".column") (clj->js {:connectWith ".column"
                                                :handle ".portlet-header"
                                                :cancel ".portlet-toggle"
                                                :placeholder
                                                             "portlet-placeholder ui-corner-all"}))
          (.. (js/$ ".portlet")
              (addClass "ui-widget ui-widget-content ui-helper-clearfix
              ui-corner-all")
              (find ".portlet-header")
              (addClass. "ui-widget-header ui-corner-all")
              (prepend "<span class='ui-icon ui-icon-plusthick
              portlet-toggle'></span>"))

          (.click (js/$ ".portlet-toggle")
                  (fn []
                    (this-as this
                      (let [icon (js/$ this)]
                            (.toggleClass icon "ui-icon-minusthick
                            ui-icon-plusthick")
                            (.toggle (.find (.closest icon ".portlet") ".portlet-content")))))))))


(defn backlog []
  (r/create-class {:reagent-render      backlog-page
                   :component-did-mount backlog-did-mount}))
