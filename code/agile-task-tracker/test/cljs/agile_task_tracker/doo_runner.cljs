(ns agile-task-tracker.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [agile-task-tracker.core-test]))

(doo-tests 'agile-task-tracker.core-test)
