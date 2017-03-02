(ns agile-task-tracker.draggable-tasks
  (:require [reagent.core :as r]))

(defn make-tasks-draggable []
  (.. (js/$ ".portlet")
      (addClass "ui-widget ui-widget-content ui-helper-clearfix
              ui-corner-all")
      (find ".portlet-header")
      (addClass. "ui-widget-header ui-corner-all")
      (prepend "<span class='ui-icon ui-icon-plusthick
              portlet-toggle'></span>"))

  (.click (js/$ ".portlet-toggle")
          (fn []
            (this-as this
              (let [icon (js/$ this)]
                (.toggleClass icon "ui-icon-minusthick
                            ui-icon-plusthick")
                (.toggle (.find (.closest icon ".portlet") ".portlet-content"))))))

  )

(defn create-task-portlet
  [task-map]
  (let [id (:task-id task-map)
        title (:task-title task-map)
        description (:description task-map)]
    [:div.portlet {:id id}
     [:div.portlet-header title]
     [:div.portlet-content description]]))