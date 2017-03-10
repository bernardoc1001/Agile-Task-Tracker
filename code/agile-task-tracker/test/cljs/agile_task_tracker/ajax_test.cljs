(ns agile-task-tracker.ajax-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [reagent.session :as session]
            [agile-task-tracker.ajax :as att-ajax]))

(deftest test-route-calculator
  (session/put! :current-page-name "backlog-page")
  (is (= "/backlog" (att-ajax/route-calculator)))

  (session/put! :current-page-name "dashboard-page")
  (is (= "/" (att-ajax/route-calculator)))

  (session/put! :current-page-name "project-page")
  (is (= "/project" (att-ajax/route-calculator)))

  (session/put! :current-page-name "current-sprint-page")
  (is (= "/current-sprint" (att-ajax/route-calculator)))

  (session/put! :current-page-name "sprints-page")
  (is (= "/sprints" (att-ajax/route-calculator))))