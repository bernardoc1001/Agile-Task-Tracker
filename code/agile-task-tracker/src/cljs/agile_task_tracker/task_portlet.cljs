(ns agile-task-tracker.task-portlet
  (:require [clojure.string :as string]
            [reagent.session :as session]
            [hipo.core :as hipo]
            [reagent-modals.modals :as rmodals]
            [ajax.core :refer [GET POST]]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
            [reagent.core :as r]
            [agile-task-tracker.common :as common]
            [goog.string :as gstring]))

(defonce edit-task
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
            :priority-level (:priority-level string-map)
            :task-state (:task-state string-map)
            :logged-time (js/parseFloat (:logged-time string-map))
            :project-id (:project-id string-map)))

(declare render-task)
(declare modal-task-editing-content)

(defn get-edit-task-for-modal-handler
  [response]
  (.log js/console (str "get-edit-task-for-modal response: " response))
  (let [task-map (convert-to-task-format (get-in response [:_source]))]
    (swap! edit-task assoc :data task-map)
    (rmodals/modal! [modal-task-editing-content])))

(defn get-edit-task-for-modal
  [task-id]
  (POST (route-calculator)
        {:params        {:data   {:task-id task-id}
                         :method "get-by-id"}
         :handler       get-edit-task-for-modal-handler
         :error-handler error-handler}))

(defn get-edit-task-by-id-handler
  [response]
  (.log js/console (str "get-edit-task-by-id response: " response))
  (let [task-map (convert-to-task-format (get-in response [:_source]))]
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
  (.log js/console (str "put-edit-task-by-id-handler response: " response))
  (get-edit-task-by-id (:_id response))
  (let [task-map (:data @edit-task)]
    (render-task task-map)))

(defn put-edit-task-by-id
  [task-map]
  (POST (route-calculator)
        {:params        {:data task-map
                         :method "put-by-id"}
         :handler       put-edit-task-by-id-handler
         :error-handler error-handler}))


(defn create-task-progressbar [task-map]
  (let [task-id (:task-id task-map)
        est-tim (:estimated-time task-map)
        log-tim (:logged-time task-map)]
    (.progressbar (js/$ (str "#progressbar-" task-id))
                  (clj->js {:value (* (/ log-tim est-tim) 100)}))))

(defn make-tasks-toggleable [task-map]
  (let [task-id (:task-id task-map)]
    (.. (js/$ ".portlet")
       (addClass "ui-widget ui-widget-content ui-helper-clearfix
              ui-corner-all")
       (find ".portlet-header")
       (addClass. "ui-widget-header ui-corner-all"))

    (.click (js/$ (str "." task-id))
            (fn []
              (this-as this
                (let [icon (js/$ this)]
                  (.toggleClass icon "ui-icon-minusthick ui-icon-plusthick")
                  (.toggle (.find (.closest icon ".portlet") ".portlet-content"))))))
    (create-task-progressbar task-map)))

(defn delete-render-portlet
  [task-id]
  (let [target-task-portlet (.getElementById js/document task-id)]
    (if (not (nil? target-task-portlet))
      (do (.remove target-task-portlet)
          true)
      false)))

(defn delete-task-by-id-handler
  [response]
  (.log js/console (str "delete-task-by-id-handler response: " response))
  (delete-render-portlet (:_id response)))

(defn delete-task-from-db
  [task-map]
  (POST (route-calculator)
        {:params        {:data   task-map
                         :method "delete-by-id"}
         :handler       delete-task-by-id-handler
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
      [:label {:for "task-title"} "Task Title: "]
      [:input {:type  "text", :class "form-control", :id "task-title",
               :value (get-in @edit-task [:data :task-title]),
                      :on-change #(common/onclick-swap-atom! edit-task [:data :task-title] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "description"} "Description "]
      [:input {:type "text", :class "form-control", :id "description",
               :default-Value (get-in @edit-task [:data :description]),
               :placeholder "Enter Description" :on-change #(common/onclick-swap-atom! edit-task [:data :description] %)}]]

     [:div {:class "form-group"}
      [:label {:for "created-by"} "Created by: "]
      [:input {:type "text", :class "form-control", :id "Created-by",
               :default-Value (get-in @edit-task [:data :created-by]),
               :placeholder "Enter Creator" :on-change #(common/onclick-swap-atom! edit-task [:data :created-by] %)}]]

     [:div {:class "form-group"}
      [:label {:for "assignees"} "Assignees: "]
      [:input {:type "text", :class "form-control", :id "Assignees",
               :default-Value (get-in @edit-task [:data :assignees]),
               :placeholder "Whoever is assigned to this task" :on-change #(common/onclick-swap-atom! edit-task [:data :assignees] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "estimated-time"} "Estimated time: "]
      [:input {:type "text", :class "form-control", :id "estimated-time",
               :default-Value (get-in @edit-task [:data :estimated-time]),
               :placeholder "Enter number of hours required" :on-change #(common/onclick-swap-atom! edit-task [:data :estimated-time] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "priority-level"} "Priority Level:"]
      [:select {:class "form-control" :id "priority"
                :defaultValue (get-in @edit-task [:data :priority-level]),  :on-change #(common/onclick-swap-atom! edit-task [:data :priority-level] %)}
       [:option {:value "Low"} "Low"]
       [:option {:value "Medium"} "Medium"]
       [:option {:value "High"} "High"]]]

     [:div {:class "form-group"}
      [:label {:for "epic"} "Epic: "]
      [:input {:type "text", :class "form-control", :id "epic",
               :default-Value (get-in @edit-task [:data :epic]),
               :placeholder "Enter Epic of task" :on-change #(common/onclick-swap-atom! edit-task [:data :epic] %)}]]


     [:div {:class "form-group"}
      [:label {:for "logged-time"} "Logged-time: "]
      [:input {:type "text", :class "form-control", :id "Logged-time",
               :default-Value (get-in @edit-task [:data :logged-time]),
               :placeholder "Enter logged time on task" :on-change #(common/onclick-swap-atom! edit-task [:data :logged-time] %)}]]]]


   [:div {:class "modal-footer"}
    [:div.btn.btn-secondary {:type         "button"
                             :data-dismiss "modal"}
     "Close"]
    [:div.btn.btn-primary {:type         "button"
                           :data-dismiss "modal"

                           :on-click     #(put-edit-task-by-id (:data @edit-task))}
     "Save"]]])

(defn create-task-portlet
  [task-map]
  (let [task-id (:task-id task-map)
        title (:task-title task-map)
        description (:description task-map)
        priority (:priority-level task-map)
        assignees (:assignees task-map)
        est (:estimated-time task-map)
        logged (:logged-time task-map)
        creator (:created-by task-map)
        epic (:epic task-map)]
    [:div.portlet {:id task-id}
     [:div.portlet-header
      [:img {:src (str "/images/" priority ".png")}]
      [:p title]
      [:span {:class (str "ui-icon ui-icon-plusthick portlet-toggle " task-id)}]]
     [:div.portlet-content
      [:p "Creator: " [:p creator]]
      [:p "Assignees: " [:p assignees]]
      [:p "Epic: " [:p epic]]
      [:p "Description: " [:p description]]
      [:div {:id (str "progressbar-" task-id)}]
      [:p (str logged " of " est " hours logged.")]

      [:div.btn.btn-primary.btn-tasklet
       {:on-click #(delete-task-from-db task-map)} "Delete Task"]
      [:div.btn.btn-primary.btn-tasklet
       {:on-click #(get-edit-task-for-modal task-id)} "Edit Tasks"]]]))




(defn get-column-id-to-render-in
  [task-state]
  (let [current-page-name (session/get :current-page-name)
        ;to-do-col in-progress-col completed-col
        ]
    (cond
      (and (= current-page-name "backlog-page") (or (= task-state "backlog-col") (string/blank? task-state)))
      (str "backlog-col")

      (and (= current-page-name "backlog-page") (= task-state "completed-col"))
      (str "do-not-render")

      (= current-page-name "backlog-page")
      (str "create-sprint-col")

      (and (= current-page-name "current-sprint-page")(= task-state "to-do-col"))
      (str "to-do-col")

      (and (= current-page-name "current-sprint-page")(= task-state "in-progress-col"))
      (str "in-progress-col")

      (and (= current-page-name "current-sprint-page")(= task-state "completed-col"))
      (str "completed-col"))))

(defn render-task
  [task-map]
  (delete-render-portlet (:task-id task-map))
  (if (nil? (.getElementById js/document (:task-id task-map)))
    (let [draggable-portlet (hipo/create (create-task-portlet task-map))
          col-id (get-column-id-to-render-in (:task-state task-map))]
      (if (not (= "do-not-render" col-id))
        (do (.appendChild (.getElementById js/document col-id) draggable-portlet)
            (make-tasks-toggleable task-map))))
    (.error js/console (str "Could not add task, old version of the task still exists"))))

