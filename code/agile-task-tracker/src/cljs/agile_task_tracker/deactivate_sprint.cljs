(ns agile-task-tracker.deactivate-sprint
  (:require [ajax.core :refer [GET POST]]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
            [agile-task-tracker.sprint-ajax :as sprint-ajax]
            [agile-task-tracker.task-ajax :as task-ajax]))

(defn deactivate-task
  [task-map]
  (hash-map :task-id (:task-id task-map)
            :task-title (:task-title task-map)
            :description (:description task-map)
            :created-by (:created-by task-map)
            :assignees (:assignees task-map)
            :estimated-time (js/parseFloat (:estimated-time task-map))
            :epic (:epic task-map)
            :sprint-id "backlog"
            :priority-level (:priority-level task-map)
            :task-state (:task-state task-map)
            :logged-time (js/parseFloat (:logged-time task-map))
            :project-id (:project-id task-map)))

(defn deactivate-sprint
  [sprint-map]
  (hash-map :sprint-id (:sprint-id sprint-map)
           :sprint-name (:sprint-name sprint-map)
           :start-date (:start-date sprint-map)
           :end-date (:end-date sprint-map)
           :project-id (:project-id sprint-map)
           :sprint-state "inactive"))

(defn deactivate-tasks-by-sprint-handler
  [response]
  (.log js/console (str "deactivate-tasks-by-sprint-handler response: " response))
  (let [hits-vector (get-in response [:hits :hits])]
    (doseq [hit hits-vector]
      (task-ajax/put-task-by-id (deactivate-task (:_source hit))))))

(defn deactivate-tasks-by-sprint-id
  [sprint-id]
  (POST (route-calculator)
        {:params        {:data   {:sprint-id sprint-id}
                         :method "query-by-term"}
         :handler       deactivate-tasks-by-sprint-handler
         :error-handler error-handler}))

(defn deactivate-sprints-handler
  [response]
  (.log js/console (str "query-active-sprint-tasks-handler response: " response))
  (let [hits-vector (get-in response [:hits :hits])]
    (if (not (empty? hits-vector))
      (doseq [hit hits-vector]
        ;deactivate the tasks
        (deactivate-tasks-by-sprint-id (get-in hit [:_source :sprint-id]))

        ;deactivate the sprint
        (sprint-ajax/put-sprint-by-id (deactivate-sprint (:_source hit)))))))

(defn deactivate-sprints-error-handler
  [response]
  (.error js/console (str "Deactivate Sprint error handler: " response)))

(defn deactivate-sprints
  [project-id]
  (POST (route-calculator)
        {:params        {:data   {:project-id project-id}
                         :method "get-all-active-sprints"}
         :handler       deactivate-sprints-handler
         :error-handler deactivate-sprints-error-handler}))