(ns agile-task-tracker.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [agile-task-tracker.task-portlet-test]
            [agile-task-tracker.ajax-test]
            [agile-task-tracker.backlog-test]
            [agile-task-tracker.deactivate-sprint-test]))

(doo-tests
  'agile-task-tracker.task-portlet-test
  'agile-task-tracker.ajax-test
  'agile-task-tracker.backlog-test
  'agile-task-tracker.deactivate-sprint-test)



