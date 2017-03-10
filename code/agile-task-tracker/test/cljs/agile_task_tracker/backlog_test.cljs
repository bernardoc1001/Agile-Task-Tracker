(ns agile-task-tracker.backlog-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [agile-task-tracker.views.backlog :as backlog :refer [new-task new-sprint current-sprint-id]]))

(def valid-task {:task-id "task-id-1"
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

(def valid-sprint {:sprint-id "sprint-id-1"
                   :sprint-name "sprint one"
                   :start-date "9/3/2017"
                   :end-date "10/3/2017"
                   :project-id "ca326"
                   :sprint-state "active"})

(deftest test-task-id-contains-white-space
  (let [input-invalid-task-id {:task-id "task id 1"
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
                               :project-id "pro-id-1"}]

    (testing "Test task id whitespace validation..."
      (testing "...with valid task id"
        (is (= false (backlog/task-id-contains-white-space valid-task))))
      (testing "...with invalid task id"
        (is (= true (backlog/task-id-contains-white-space input-invalid-task-id)))))))

(deftest test-validate-task
  (let [empty-id {:task-id ""
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
                  :project-id "pro-id-1"}

        whitespace-title {:task-id "task-id-1"
                         :task-title "      "
                         :description "This is a description"
                         :created-by "Ben"
                         :assignees "John"
                         :estimated-time 5
                         :epic "First Epic"
                         :sprint-id "sprint-id-1"
                         :priority-level "medium"
                         :task-state "backlog-col"
                         :logged-time 3
                         :project-id "pro-id-1"}

        nil-created-by {:task-id "task-id-1"
                        :task-title "task title 1"
                        :description "This is a description"
                        :created-by nil
                        :assignees "John"
                        :estimated-time 5
                        :epic "First Epic"
                        :sprint-id "sprint-id-1"
                        :priority-level "medium"
                        :task-state "backlog-col"
                        :logged-time 3
                        :project-id "pro-id-1"}]

    (testing "Test if required user inputted fields are present..."
      (testing "...with all required fields present"
        (is (= true (backlog/validate-task valid-task))))
      (testing "...with required field empty"
        (is (= false (backlog/validate-task empty-id))))
      (testing "...with required field as all whitespaces"
        (is (= false (backlog/validate-task whitespace-title))))
      (testing "...with required field as nil"
        (is (= false (backlog/validate-task nil-created-by)))))))


(deftest test-update-unassociated-task
  (let [inputted-string-map {:task-id        "task-id-1"
                             :task-title     "task title 1"
                             :description    "This is a description"
                             :created-by     "Ben"
                             :assignees      "John"
                             :estimated-time "5"
                             :epic           "First Epic"
                             :sprint-id      "backlog"
                             :priority-level "medium"
                             :task-state     "backlog-col"
                             :logged-time    "3"
                             :project-id     "pro-id-1"}

        expected-result {:task-id        "task-id-1"
                         :task-title     "task title 1"
                         :description    "This is a description"
                         :created-by     "Ben"
                         :assignees      "John"
                         :estimated-time 5
                         :epic           "First Epic"
                         :sprint-id      "updated-sprint-id-5000" ;updated value
                         :priority-level "medium"
                         :task-state     "to-do-col"
                         :logged-time    3
                         :project-id     "pro-id-1"}]

    ;put pre-requisit value in the current-sprint-id atom
    (swap! current-sprint-id assoc :sprint-id "updated-sprint-id-5000")

    ;perform test
    (testing "Test updating unassigned tasks to a sprint"
      (is (= expected-result (backlog/update-unassociated-task inputted-string-map))))

    ;reset atom after the test
    (reset! current-sprint-id {})))


(deftest test-validate-sprint
  (let [empty-id {:sprint-id ""
                  :sprint-name "sprint one"
                  :start-date "9/3/2017"
                  :end-date "10/3/2017"
                  :project-id "ca326"
                  :sprint-state "active"}

        whitespace-name {:sprint-id "sprint-id-1"
                         :sprint-name "       "
                         :start-date "9/3/2017"
                         :end-date "10/3/2017"
                         :project-id "ca326"
                         :sprint-state "active"}

        nil-project-id {:sprint-id "sprint-id-1"
                        :sprint-name "sprint one"
                        :start-date "9/3/2017"
                        :end-date "10/3/2017"
                        :project-id nil
                        :sprint-state "active"}]

    (testing "Test if required user inputted fields are present..."
      (testing "...with all required fields present"
        (is (= true (backlog/validate-sprint valid-sprint))))
      (testing "...with required field empty"
        (is (= false (backlog/validate-sprint empty-id))))
      (testing "...with required field as all whitespaces"
        (is (= false (backlog/validate-sprint whitespace-name))))
      (testing "...with required field as nil"
        (is (= false (backlog/validate-sprint nil-project-id)))))))

(deftest test-sprint-id-contains-white-space
  (let [input-invalid-sprint-id {:sprint-id "sprint id 1"
                                 :sprint-name "sprint one"
                                 :start-date "9/3/2017"
                                 :end-date "10/3/2017"
                                 :project-id "ca326"
                                 :sprint-state "active"}]

    (testing "Test sprint id whitespace validation..."
      (testing "...with valid sprint id"
        (is (= false (backlog/sprint-id-contains-white-space valid-sprint))))
      (testing "...with invalid sprint id"
        (is (= true (backlog/sprint-id-contains-white-space input-invalid-sprint-id)))))))


