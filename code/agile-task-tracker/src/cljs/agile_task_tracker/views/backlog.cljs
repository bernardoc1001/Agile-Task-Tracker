(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]))

(defonce page-state
         (r/atom {:tasks []}))

(defonce new-task
         (r/atom {}))

(defn save-task-procedure
  [task]
  (let [updated-task-list (conj (:tasks @page-state) task)]
    (println (str updated-task-list))
    (reset! new-task {})
    (swap! page-state assoc-in [:tasks] updated-task-list)))

(defn modal-task-creation-content []
  (reset! new-task {})
  [:div
   [:div {:class "modal-content"}
    [:div {:class "modal-header"}
     [:h5 {:class "modal-title"
           :id "task-modal-title"}
      "Create a task"]]
    [:div {:class "modal-body"}

     [:p " id" [:input {:type      "text"
                              :name      "taskid"
                              :on-change #(common/update-atom! new-task [:task-id] %)}]]

     [:p " title" [:input {:type      "text"
                        :name      "title"
                        :on-change #(common/update-atom! new-task [:task-title] %)}]]

     [:p " description" [:input {:type      "text"
                                       :name      "description"
                                       :on-change #(common/update-atom! new-task [:description] %)}]]


     [:p " created-by" [:input {:type      "text"
                                      :name      "created-by"
                                      :on-change #(common/update-atom! new-task [:created-by] %)}]]

     [:p " assignees" [:input {:type      "text"
                                     :name      "assignees"
                                     :on-change #(common/update-atom! new-task [:assignees] %)}]]

     [:p " original estimate" [:input {:type      "text"
                                             :name      "original-estimate"
                                             :on-change #(common/update-atom! new-task [:original-estimate] %)}]]

     [:p " remaining estimate" [:input {:type      "text"
                                              :name      "remaining-estimate"
                                              :on-change #(common/update-atom! new-task [:remaining-estimate] %)}]]
     [:p " epic" [:input {:type      "text"
                                :name      "epic"
                                :on-change #(common/update-atom! new-task [:epic] %)}]]

     [:p " assigned sprint" [:input {:type      "text"
                                           :name      "assigned-sprint"
                                           :on-change #(common/update-atom! new-task [:assigned-sprint] %)}]]


     [:p " priority level" [:input {:type      "text"
                                          :name      "priority-level"
                                          :on-change #(common/update-atom! new-task [:priority-level] %)}]]

     [:p " task state" [:input {:type      "text"
                                      :name      "task-state"
                                      :on-change #(common/update-atom! new-task [:task-state] %)}]]
     [:p " logged time" [:input {:type      "text"
                                       :name      "logged-time"
                                       :on-change #(common/update-atom! new-task [:logged-time] %)}]]]

    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type     "button"
                            :data-dismiss "modal"
                            :on-click #(save-task-procedure @new-task)}
      "Save"]]]])

(defn modal-window-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [modal-task-creation-content])}
   "Create Task"])

(defn backlog-page []
  [:div
   [:p (str "page-state: " @page-state)]
   [:p (str "new-task: " @new-task)]
   [:p "backlog test"]

   [:div
    [rmodals/modal-window]
    [modal-window-button]]

   [:p [:a {:href "#/"} "Back to Dashboard"]]])

