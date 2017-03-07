(ns agile-task-tracker.task-ajax
  (:require  [ajax.core :refer [GET POST]]
             [agile-task-tracker.task-portlet :as task-portlet]))


(defn handler
  [response]
  (.log js/console (str "handler response: " response)))

(defn error-handler
  [response]
  (.error js/console (str response)))



(defn get-task-by-id-handler
  [response]
  (.log js/console (str "get-task-by-id-handler response: " response))
  (let [task-map (task-portlet/convert-to-task-format (get-in response [:_source]))]
    (task-portlet/render-task task-map)))


(defn get-task-by-id
  [route task-id]
  (POST route
        {:params        {:data   {:task-id task-id}
                         :method "get-by-id"}
         :handler       get-task-by-id-handler
         :error-handler error-handler}))

(defn put-task-by-id-handler
  [response]
  (.log js/console (str "put-task-handler response: " response))
  ;task will be rendered in the get response handler
  ;TODO calculate the route here for get-task-by-id
  (get-task-by-id "/backlog" (:_id response)))

(defn put-task-by-id
  [route task-map]
  (POST route
        {:params        {:data task-map
                         :method "put-by-id"}
         :handler       put-task-by-id-handler
         :error-handler error-handler}))

(defn delete-task-by-id-handler
  [response]
  (.log js/console (str "delete-task-by-id-handler response: " response))
  (task-portlet/delete-task-portlet (:_id response)))

(defn delete-task-by-id
  [route task-map]
  (POST route
        {:params        {:data   task-map
                         :method "delete-by-id"}
         :handler       delete-task-by-id-handler
         :error-handler error-handler}))

(defn query-tasks-by-sprint-handler
  [response]
  (.log js/console (str "query-task-handler response: " response))
  (let [hits-vector (get-in response [:hits :hits])]
    (doseq [hit hits-vector]
      (task-portlet/render-task (task-portlet/convert-to-task-format (:_source hit))))))

(defn query-tasks-by-sprint
  [route sprint-id]
  (POST route
        {:params        {:data   {:sprint-id sprint-id}
                         :method "query-by-term"}
         :handler       query-tasks-by-sprint-handler
         :error-handler error-handler}))