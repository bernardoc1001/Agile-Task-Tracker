(ns agile-task-tracker.task-portlet-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [agile-task-tracker.task-portlet :as task-portlet :refer [edit-task]]))


(defonce unconverted-task-map{:task-id "task-id-1"
                              :task-title "task title 1"
                              :description "This is a description"
                              :created-by "Ben"
                              :assignees "John"
                              :estimated-time "5"
                              :epic "First Epic"
                              :sprint-id "sprint-id-1"
                              :priority-level "medium"
                              :task-state "backlog-col"
                              :logged-time "3"
                              :project-id "pro-id-1"})

(defonce converted-task-map {:task-id "task-id-1"
                             :task-title "task title 1"
                             :description "This is a description"
                             :created-by "Ben"
                             :assignees "John"
                             :estimated-time 5
                             :epic "First Epic"
                             :sprint-id "sprint-id-1"
                             :priority-level "medium"
                             :task-state "backlog-col"
                             :logged-time 3
                             :project-id "pro-id-1"})

(deftest test-convert-to-task-format
  (let [test-input unconverted-task-map
        expected-result converted-task-map]
    (is (= expected-result (task-portlet/convert-to-task-format test-input)))))


(deftest test-get-edit-task-by-id-handler
  (let [response    {:_index   "task-info",
                    :_type    "task-info-mapping",
                    :_id      "task-id-1",
                    :_version 2,
                    :found    true,
                    :_source  unconverted-task-map}

        expected-result converted-task-map]

    ;ensure the atom is clear before the test
    (reset! edit-task {})

    (task-portlet/get-edit-task-by-id-handler response)
    (is (= expected-result (:data @edit-task)))

    ;ensure the atom is clear after the test
    (reset! edit-task {})))



