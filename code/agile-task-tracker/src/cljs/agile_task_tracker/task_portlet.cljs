(ns agile-task-tracker.task-portlet
  (:require [clojure.string :as string]
            [reagent.session :as session]
            [hipo.core :as hipo]))

(defn convert-to-task-format
  [string-map]
  (hash-map  :task-id (:task-id string-map)
            :task-title (:task-title string-map)
            :description (:description string-map)
            :created-by (:created-by string-map)
            :assignees (:assignees string-map)
            :estimated-time (js/parseFloat (:estimated-time string-map))
            :epic (:epic string-map)
            :sprint-id (:sprint-id string-map)
            :priority-level (:priority-level string-map)
            :task-state (:task-state string-map)
            :logged-time (js/parseFloat (:logged-time string-map))
            :project-id (:project-id string-map)))

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

(defn get-column-id-to-render-in
  [task-state]
  (let [current-page-name (session/get :current-page-name)
        ;TODO add in sprint page
        ]
    (cond
      (and (= current-page-name "backlog-page") (or (= task-state "backlog-col") (string/blank? task-state)))
      (str "backlog-col")

      (= current-page-name "backlog-page")
      (str "create-sprint-col"))))

(defn render-task
  [task-map]
  (delete-task-portlet (:task-id task-map))
  (if (nil? (.getElementById js/document (:task-id task-map)))
    (let [draggable-portlet (hipo/create (create-task-portlet task-map))
          col-id (get-column-id-to-render-in (:task-state task-map))]
      (.appendChild (.getElementById js/document col-id) draggable-portlet)
      (make-tasks-toggleable task-map))
    (.error js/console (str "Could not add task, old version of the task still exists"))))

