(ns agile-task-tracker.sprint-ajax
  (:require [ajax.core :refer [GET POST]]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]))


(defn put-sprint-by-id-handler
  [response]
  (.log js/console (str "put-sprint-handler response: " response)))

(defn put-sprint-by-id
  [sprint-map]
  (POST (route-calculator)
        {:params        {:data sprint-map
                         :method "put-sprint-by-id"}
         :handler       put-sprint-by-id-handler
         :error-handler error-handler}))



