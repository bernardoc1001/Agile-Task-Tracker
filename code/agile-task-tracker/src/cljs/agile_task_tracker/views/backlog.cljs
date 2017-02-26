(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [ajax.core :refer [GET POST]]
            [goog.string :as gstring]))

(defonce page-state
         (r/atom {:tasks []}))

(defonce new-task
         (r/atom {}))
;-----------test ajax stuff, refactor this -------------------------
(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "errorhandler- something bad happened: " status " " status-text)))
;-------------------------------------------------------------------
(defn save-task-procedure
  [task]
  (let [updated-task-list (conj (:tasks @page-state) task)]
    (swap! page-state assoc-in [:tasks] updated-task-list)
    (POST "/backlog"
          {:params        (hash-map :all-tasks (:tasks @page-state))
           :handler       handler
           :error-handler error-handler})))

(defn atom-input-field
  ([label type atom path]
   [:label label [:input {:type      type
                          :name      label
                          :on-change #(common/onclick-swap-atom! atom path %)}]])
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

    [:div [atom-input-field "Task ID: " new-task [:task-id]]]
    [:div [atom-input-field "Title: " new-task [:task-title]]]
    [:div [atom-input-field "Description: " new-task [:description]]]
    [:div [atom-input-field "Created By: " new-task [:created-by]]]
    [:div [atom-input-field "Assignees: " new-task [:assignees]]]
    [:div [atom-input-field "Original Estimate: " new-task [:original-estimate]]]
    [:div [atom-input-field "Remaining Estimate: " new-task [:remaining-estimate]]]
    [:div [atom-input-field "Epic: " new-task [:epic]]]
    [:div [atom-input-field "Assigned Sprint: " new-task [:assigned-sprint]]]
    [:div [atom-input-field "Priority Level: " new-task [:priority-level]]]
    [:div [atom-input-field "Task State: " new-task [:task-state]]]
    [:div [atom-input-field "Logged Time: " new-task [:logged-time]]]

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

(defn backlog-page []
       [:div
        [:p (str "page-state: " @page-state)]
        [:p (str "new-task: " @new-task)]
        [:p "backlog test"]

        [:div {:class "container"}
         [:div {:class "row"}
          [:div {:class "col-md-4"}
           [:div {:class "panel panel-default"}
            [:div {:class "panel-heading"} "Backlog"]
            [:div {:class "panel-body"}
             [:div {:class "panel panel-default"}
              [:div {:class "panel-body"}
               [:div
                [rmodals/modal-window]
                [modal-window-button]]
               [:div {:id    "draggable"
                      :class "ui-widget-content"
                      :style {:width   "100px"
                              :heigwht  "30px"
                              :padding "0.5em"
                              :zIndex "1"}}
                [:p "Drag me"]]]]]]]


          [:div {:class "col-md-8"}
            [:div {:class "panel panel-default"}
               [:div {:class "panel-heading"} "Sprint Creation/Modification"]
             [:div {:class "panel-body"}
              [:div {:class "col-md-6"}
               [:div {:class "panel panel-default"}
                [:div {:class "panel-body"}
                 [:div [rmodals/modal-window]
                  [modal-window-button]]]]]

              [:div {:class "col-md-6"}
               [:div {:class "panel panel-default"}
                [:div {:class "panel-body"}
                 [:div [atom-input-field "Sprint Name " new-task [:sprint-name]]]]]]]]]

          [:a {:href "/"} "Back to Homepage"]]]])

(defn backlog-did-mount []
  (.draggable (js/$ "#draggable")))


(defn backlog []
  (r/create-class {:reagent-render      backlog-page
                   :component-did-mount backlog-did-mount}))
