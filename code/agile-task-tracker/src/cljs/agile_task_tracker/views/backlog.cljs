(ns agile-task-tracker.views.backlog
  (:require [reagent.core :as r]))

(defonce page-state
         (r/atom {:tasks [{:task-id "1"
                           :description ""
                           }
                          {:task-id "2"
                           :description ""}]}))

(defn backlog-page []
  [:div
   [:p (str @page-state)]
   [:p "backlog test"]
   
   [:p "task 1 id" [:input {:type      "text"
                            :name      "taskid"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :task-id] (-> % .-target .-value))}]]

   [:p "task 1 description" [:input {:type      "text"
                            :name      "description"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 0 :description] (-> % .-target .-value))}]]


   [:p "task 2 id" [:input {:type      "text"
                            :name      "taskid"
                            :on-change #(swap! page-state assoc-in
                                               [:tasks 1 :task-id] (-> % .-target .-value))}]]

   [:p "task 2 description" [:input {:type      "text"
                                     :name      "description"
                                     :on-change #(swap! page-state assoc-in
                                                        [:tasks 1
                                                         :description] (-> % .-target .-value))}]]

   [:p [:a {:href "#/"} "Back to Dashboard"]]])
