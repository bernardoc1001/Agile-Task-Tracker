(ns agile-task-tracker.sprint-ajax
  (:require [ajax.core :refer [GET POST]]
            [agile-task-tracker.task-portlet :as task-portlet]))


(defn handler
  [response]
  (.log js/console (str "handler response: " response)))

(defn error-handler
  [response]
  (.error js/console (str response)))

(defn get-sprint-by-id-handler
  [response]
  (.log js/console (str "get-sprint-by-id-handler response: " response)))
;TODO review

(defn get-sprint-by-id
  [route sprint-id]
  (POST route
        {:params        {:data   {:sprint sprint-id}
                         :method "get-sprint-by-id"}
         :handler       get-sprint-by-id-handler
         :error-handler error-handler}))

(defn put-sprint-by-id-handler
  [response]
  (.log js/console (str "put-sprint-handler response: " response)))

(defn put-sprint-by-id
  [route sprint-map]
  (POST route
        {:params        {:data sprint-map
                         :method "put-sprint-by-id"}
         :handler       put-sprint-by-id-handler
         :error-handler error-handler}))

(defn delete-sprint-by-id-handler
  [response]
  (.log js/console (str "delete-sprint-by-id-handler response: " response)))

(defn delete-sprint-by-id
  [route task-map]
  (POST route
        {:params        {:data   task-map
                         :method "delete-by-id"}
         :handler       delete-sprint-by-id-handler
         :error-handler error-handler}))

(defn query-sprint-by-sprint-handler
  [response]
  (.log js/console (str "query-task-handler response: " response)))

(defn query-sprint-by-sprint
  [route project-id]
  (POST route
        {:params        {:data   {:project-id project-id}
                         :method "query-by-term"}
         :handler       query-sprint-by-sprint-handler
         :error-handler error-handler}))