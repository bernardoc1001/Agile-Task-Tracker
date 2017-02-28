(ns agile-task-tracker.views.dashboard
  (:require [reagent.core :as r]
						[agile-task-tracker.sidebar :as sidebar]))

(defn dashboard-page []
  [:div
	 [:div#wrapper
		[sidebar/sidebar]

		[:div.page-content-wrapper>div.container-fluid>div.row.div-lg12
		 [:div.jumbotron [:h2 "Agile Task Tracker"]]

		 [:div {:class "panel panel-default"}
			[:div {:class "panel-body"}
			 [sidebar/menu-toggle]
			 [:p "Yeah this is the landing page"]]]]]])