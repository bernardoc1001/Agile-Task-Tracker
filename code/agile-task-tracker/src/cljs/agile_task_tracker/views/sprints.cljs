(ns agile-task-tracker.views.sprints
	(:require [reagent.core :as r]
						[agile-task-tracker.sidebar :as sidebar]))

(defn sprints-page []
	[:div
	 [:div#wrapper
		[sidebar/sidebar]

		[:div.page-content-wrapper>div.container-fluid>div.row.div-lg12


		 [:div {:class "panel panel-default"}
			[:div {:class "panel-body"}
			 [sidebar/menu-toggle]
			 [:p "Yeah this is the sprint page"]]]]]])
