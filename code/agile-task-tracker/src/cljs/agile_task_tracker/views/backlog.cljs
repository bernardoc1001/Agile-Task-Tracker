(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]))

(defonce page-state
         (r/atom {:tasks []}))

#_(defn modal-task-creation-content []
  [:div])

(defn modal-window-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [:div "some message to the user!"])}
   "My Modal"])

(defn backlog-page []
  [:div
   [:p (str @page-state)]
   [:p "backlog test"]
   
   [:p "task 1 id" [:input {:type      "text"
                            :name      "taskid"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :task-id] (-> % .-target .-value))}]]

   [:p "task 1 description" [:input {:type "text"
                            :name      "description"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :description] (-> % .-target .-value))}]]


   [:p "task 1 created-by" [:input {:type      "text"
                            :name      "created-by"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :created-by] (-> % .-target .-value))}]]

   [:p "task 1 assignees" [:input {:type      "text"
                                   :name      "assignees"
                                   :on-change #(swap! page-state assoc-in
                                                      [:tasks 0 :assignees]  (-> % .-target .-value))}]]

   [:p "task 1 original estimate" [:input {:type      "text"
                            :name      "original-estimate"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :original-estimate] (-> % .-target .-value))}]]

   [:p "task 1 remaining estimate" [:input {:type      "text"
                                     :name      "remaining-estimate"
                                     :on-change #(swap! page-state assoc-in
                                                        [:tasks 0 :remaining-estimate] (-> % .-target .-value))}]]
   [:p "task 1 epic" [:input {:type      "text"
                            :name      "epic"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :epic] (-> % .-target .-value))}]]

   [:p "task 1 assigned sprint" [:input {:type      "text"
                                     :name      "assigned-sprint"
                                     :on-change #(swap! page-state assoc-in
                                                        [:tasks 0 :assigned-sprint] (-> % .-target .-value))}]]


   [:p "task 1 priority level" [:input {:type      "text"
                            :name      "priority-level"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :priority-level] (-> % .-target .-value))}]]

   [:p "task 1 task state" [:input {:type      "text"
                                     :name      "task-state"
                                     :on-change #(swap! page-state assoc-in
                                                        [:tasks 0 :task-state] (-> % .-target .-value))}]]
   [:p "task 1 logged time" [:input {:type "text"
                                     :name "logged-time"
                                     :on-change #(swap! page-state assoc-in
                                                        [:tasks 0 :logged-time] (-> % .-target .-value))}]]

   [:div
    [rmodals/modal-window]
    [modal-window-button]]

   [:p [:a {:href "#/"} "Back to Dashboard"]]])

