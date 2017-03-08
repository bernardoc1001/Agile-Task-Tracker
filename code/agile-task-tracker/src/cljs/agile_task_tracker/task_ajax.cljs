(ns agile-task-tracker.task-ajax
  (:require  [ajax.core :refer [GET POST]]
             [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
             [agile-task-tracker.task-portlet :as task-portlet]))

(defn get-task-by-id-handler
  [response]
  (.log js/console (str "get-task-by-id-handler response: " response))
  (let [task-map (task-portlet/convert-to-task-format (get-in response [:_source]))]
    (task-portlet/render-task task-map)))


(defn get-task-by-id
  [task-id]
  (POST (route-calculator)
        {:params        {:data   {:task-id task-id}
                         :method "get-by-id"}
         :handler       get-task-by-id-handler
         :error-handler error-handler}))

(defn put-task-by-id-handler
  [response]
  (.log js/console (str "put-task-handler response: " response))
  ;task will be rendered in the get response handler
  ;TODO calculate the route here for get-task-by-id
  (get-task-by-id (:_id response)))

(defn put-task-by-id
  [task-map]
  (POST (route-calculator)
        {:params        {:data task-map
                         :method "put-by-id"}
         :handler       put-task-by-id-handler
         :error-handler error-handler}))


(defn query-tasks-by-sprint-handler
  [response]
  (.log js/console (str "query-task-handler response: " response))
  (let [hits-vector (get-in response [:hits :hits])]
    (doseq [hit hits-vector]
      (task-portlet/render-task (task-portlet/convert-to-task-format (:_source hit))))))

(defn query-tasks-by-sprint
  [sprint-id]
  (POST (route-calculator)
        {:params        {:data   {:sprint-id sprint-id}
                         :method "query-by-term"}
         :handler       query-tasks-by-sprint-handler
         :error-handler error-handler}))