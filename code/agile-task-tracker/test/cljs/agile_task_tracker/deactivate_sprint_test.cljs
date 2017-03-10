(ns agile-task-tracker.deactivate-sprint-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [agile-task-tracker.deactivate-sprint :as deactivate-sprint]))

(def valid-task {:task-id "task-id-1"
                 :task-title "task title 1"
                 :description "This is a description"
                 :created-by "Ben"
                 :assignees "John"
                 :estimated-time 5
                 :epic "First Epic"
                 :sprint-id "sprint-id-1"
                 :priority-level "medium"
                 :task-state "to-do-col"
                 :logged-time 3
                 :project-id "pro-id-1"})

(def valid-sprint {:sprint-id "sprint-id-1"
                   :sprint-name "sprint one"
                   :start-date "9/3/2017"
                   :end-date "10/3/2017"
                   :project-id "ca326"
                   :sprint-state "active"})

(deftest test-deactivate-task
  (let [expected-out {:task-id "task-id-1"
                      :task-title "task title 1"
                      :description "This is a description"
                      :created-by "Ben"
                      :assignees "John"
                      :estimated-time 5
                      :epic "First Epic"
                      :sprint-id "backlog"
                      :priority-level "medium"
                      :task-state "create-sprint-col"
                      :logged-time 3
                      :project-id "pro-id-1"}]
    (is (= expected-out (deactivate-sprint/deactivate-task valid-task)))))

(deftest test-deactivate-sprint
  (let [expected-out {:sprint-id "sprint-id-1"
                      :sprint-name "sprint one"
                      :start-date "9/3/2017"
                      :end-date "10/3/2017"
                      :project-id "ca326"
                      :sprint-state "inactive"}]
    (is (= expected-out (deactivate-sprint/deactivate-sprint valid-sprint)))))

