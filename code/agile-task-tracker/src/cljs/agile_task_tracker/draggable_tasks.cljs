(ns agile-task-tracker.draggable-tasks
  (:require [reagent.core :as r]))

(defn create-task-progressbar [task-map]
  (let [task-id (:task-id task-map)
        est-tim (:estimated-time task-map)
        log-tim (:logged-time task-map)]
    (.progressbar (js/$ (str "#progressbar-" task-id))
                  (clj->js {:value (* (/ log-tim est-tim) 100)}))))

(defn make-tasks-toggleable [task-map]
  (let [task-id (:task-id task-map)]
    (.. (js/$ ".portlet")
       (addClass "ui-widget ui-widget-content ui-helper-clearfix
              ui-corner-all")
       (find ".portlet-header")
       (addClass. "ui-widget-header ui-corner-all"))

    (.click (js/$ (str "." task-id))
            (fn []
              (this-as this
                (let [icon (js/$ this)]
                  (.toggleClass icon "ui-icon-minusthick ui-icon-plusthick")
                  (.toggle (.find (.closest icon ".portlet") ".portlet-content"))))))
    (create-task-progressbar task-map)))

(defn create-task-portlet
  [task-map]
  (let [task-id (:task-id task-map)
        title (:task-title task-map)
        description (:description task-map)
        priority (:priority-level task-map)]
    [:div.portlet {:id task-id}
     [:div.portlet-header
      [:img {:src (str "/images/" priority ".png")}]
      [:p title]
      [:span {:class (str "ui-icon ui-icon-plusthick portlet-toggle "task-id)}]]
     [:div.portlet-content description
      [:div {:id (str "progressbar-" task-id)} ]]]))

(defn delete-task-portlet
  [task-id]
  (let [target-task-portlet (.getElementById js/document task-id)]
    (if (not (nil? target-task-portlet))
      (do (.remove target-task-portlet)
          true)
      false)))


