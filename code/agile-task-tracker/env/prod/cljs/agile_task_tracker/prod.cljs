(ns agile-task-tracker.prod
  (:require [agile-task-tracker.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
