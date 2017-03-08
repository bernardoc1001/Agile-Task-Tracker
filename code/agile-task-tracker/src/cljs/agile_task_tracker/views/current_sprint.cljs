(ns agile-task-tracker.views.current-sprint
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.common :as common]
            [goog.string :as gstring]
            [agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.task-ajax :as task-ajax]
    ;[agile-task-tracker.sprint-ajax :as sprint-ajax]
            [agile-task-tracker.sortable :as sortable]
            [reagent.session :as session]
            [clojure.string :as string]))

(defn current-sprint-page []
  [:div
   [:div#wrapper
    [sidebar/sidebar]

    [:div.page-content-wrapper>div.container-fluid>div.row.div-lg12


     [:div {:class "panel panel-default"}
      [:div {:class "panel-body"}
       [sidebar/menu-toggle]
       [:p "Yeah this is the current sprint page"]]]]]])

(defn current-sprint-did-mount []
  (sortable/sortable-column))

(defn current-sprint []
  (r/create-class {:reagent-render      current-sprint-page
                   :component-did-mount current-sprint-did-mount}))
