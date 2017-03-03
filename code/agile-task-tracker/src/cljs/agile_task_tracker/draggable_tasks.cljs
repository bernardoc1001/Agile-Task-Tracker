(ns agile-task-tracker.draggable-tasks
  (:require [reagent.core :as r]))

(defn make-tasks-toggleable [task-id]
  (.. (js/$ ".portlet")
      (addClass "ui-widget ui-widget-content ui-helper-clearfix
              ui-corner-all")
      (find ".portlet-header")
      (addClass. "ui-widget-header ui-corner-all"))
  (.log js/console task-id)
  (.click (js/$ (str "." task-id))
          (fn []
            (this-as this
              (let [icon (js/$ this)]
                (.toggleClass icon "ui-icon-minusthick ui-icon-plusthick")
                (.toggle (.find (.closest icon ".portlet") ".portlet-content")))))))

(defn create-task-portlet
  [task-map]
  (let [task-id (:task-id task-map)
        title (:task-title task-map)
        description (:description task-map)]
    [:div.portlet {:id task-id}
     [:div.portlet-header title
      [:span {:class (str "ui-icon ui-icon-plusthick portlet-toggle "task-id)}]]
     [:div.portlet-content
      [:div {:class "progress"}
       [:div {:class "progress-bar"
              :role "progressbar"
              :aria-valuenow "70"
              :aria-valuemin "0"
              :aria-valuemax "100"
              :style {:width "70%"}} "70% Complete"]] description ]]))



