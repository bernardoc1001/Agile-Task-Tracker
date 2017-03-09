(ns agile-task-tracker.editable-task-portlet
  (:require [ajax.core :refer [GET POST]]
            [agile-task-tracker.task-portlet :as task-portlet]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
            [reagent.core :as r]
            [agile-task-tracker.common :as common]
            [goog.string :as gstring]))

(defonce edit-task
         (r/atom {:data {}}))


(defn get-edit-task-by-id-handler
  [response]
  (.log js/console (str "get-edit-task-by-id-handler response: " response))
  (let [task-map (task-portlet/convert-to-task-format (get-in response [:_source]))]
    (swap! edit-task assoc :data task-map)))


(defn get-edit-task-by-id
  [task-id]
  (POST (route-calculator)
        {:params        {:data   {:task-id task-id}
                         :method "get-by-id"}
         :handler       get-edit-task-by-id-handler
         :error-handler error-handler}))

(defn put-edit-task-by-id-handler
  [response]
  (.log js/console (str "put-task-handler response: " response))
  (let [task-map (task-portlet/convert-to-task-format (get-in response [:_source]))]
   (task-portlet/render-task task-map) ))

(defn put-edit-task-by-id
  [task-map]
  (POST (route-calculator)
        {:params        {:data task-map
                         :method "put-by-id"}
         :handler       put-edit-task-by-id-handler
         :error-handler error-handler}))

(defn modal-task-editing-content []
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
    [:form
     [:div {:class "form-group"}
      [:label {:for "task-tile"} "Task Title: "]
      [:input {:type "text", :class "form-control", :id "task-title",
               :default-Value (:task-title @edit-task),
               :placeholder "Enter Task Title" :on-change #(common/onclick-swap-atom! edit-task [:data :task-title] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "description"} "Description "]
      [:input {:type "text", :class "form-control", :id "description",
               :default-Value (:description @edit-task),
               :placeholder "Enter Description" :on-change #(common/onclick-swap-atom! edit-task [:data :description] %)}]]

     [:div {:class "form-group"}
      [:label {:for "created-by"} "Created by: "]
      [:input {:type "text", :class "form-control", :id "Created-by",
               :default-Value (:created-by @edit-task),
               :placeholder "Enter Creator" :on-change #(common/onclick-swap-atom! edit-task [:data :created-by] %)}]]

     [:div {:class "form-group"}
      [:label {:for "assignees"} "Assignees: "]
      [:input {:type "text", :class "form-control", :id "Assignees",
               :default-Value (:assignees @edit-task),
               :placeholder "Whoever is assigned to this task" :on-change #(common/onclick-swap-atom! edit-task [:data :assignees] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "estimated-time"} "Estimated time: "]
      [:input {:type "text", :class "form-control", :id "estimated-time",
               :default-Value (:estimated-time @edit-task),
               :placeholder "Enter number of hours required" :on-change #(common/onclick-swap-atom! edit-task [:data :estimated-time] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "priority-level"} "Priority Level:"]
      [:select {:class "form-control" :id "priority"
                :defaultValue (:priority-level @edit-task),  :on-change #(common/onclick-swap-atom! edit-task [:data :priority-level] %)}
       [:option {:value "Low"} "Low"]
       [:option {:value "Medium"} "Medium"]
       [:option {:value "High"} "High"]]]

     [:div {:class "form-group"}
      [:label {:for "epic"} "Epic: "]
      [:input {:type "text", :class "form-control", :id "epic",
               :default-Value (:epic @edit-task),
               :placeholder "Enter Epic of task" :on-change #(common/onclick-swap-atom! edit-task [:data :epic] %)}]]


     [:div {:class "form-group"}
      [:label {:for "logged-time"} "Logged-time: "]
      [:input {:type "text", :class "form-control", :id "Logged-time",
               :default-Value (:logged-time @edit-task),
               :placeholder "Enter logged time on task" :on-change #(common/onclick-swap-atom! edit-task [:data :logged-time] %)}]]]]


   [:div {:class "modal-footer"}
    [:div.btn.btn-secondary {:type         "button"
                             :data-dismiss "modal"}
     "Close"]
    [:div.btn.btn-primary {:type         "button"
                           :data-dismiss "modal"

                           :on-click     #(put-edit-task-by-id (:data @edit-task))}
     "Save"]]])

