(ns agile-task-tracker.sortable
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
            [agile-task-tracker.task-portlet :as task-portlet]))

(defonce task-state
         (r/atom {}))


(defn put-updated-state-handler
  [response]
  ;do nothing
  )

(defn put-updated-state
  [task-map]
  (POST (route-calculator)
        {:params        {:data   task-map
                         :method "put-by-id"}
         :handler       put-updated-state-handler
         :error-handler error-handler}))

(defn get-updated-state-handler
  [response]
  (let [task-map (task-portlet/convert-to-task-format (get-in response [:_source]))]
    (put-updated-state (assoc task-map :task-state (:task-state @task-state)))))

(defn get-updated-state
  [task-id]
  (POST (route-calculator)
        {:params        {:data   {:task-id task-id}
                         :method "get-by-id"}
         :handler       get-updated-state-handler
         :error-handler error-handler}))


(defn sortable-column []
  (js/$ (fn []
          (.sortable (js/$ ".column") (clj->js {:connectWith ".column"
                                                :handle      ".portlet-header"
                                                :cancel      ".portlet-toggle"
                                                :placeholder "portlet-placeholder ui-corner-all"
                                                :receive     (fn [event ui]
                                                               (let [task-id (.-id (aget (.-item ui) "0"))
                                                                     column-id (.-id (.-parentElement (aget (.-item ui) "0")))]
                                                                 (swap! task-state assoc :task-state column-id)
                                                                 (get-updated-state task-id)))})))))