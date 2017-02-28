(ns agile-task-tracker.views.project
	(:require [reagent.core :as r]
						[reagent-modals.modals :as rmodals]
						[agile-task-tracker.common :as common]
						[ajax.core :refer [GET POST]]
						[goog.string :as gstring]
						[agile-task-tracker.sidebar :as sidebar]))

(defn project-page []
	[:div
	 [:div#wrapper
		[sidebar/sidebar]

		[:div.page-content-wrapper>div.container-fluid>div.row.div-lg12


		 [:div {:class "panel panel-default"}
			[:div {:class "panel-body"}
			 [sidebar/menu-toggle]
			 [:p "Yeah this is the project page"]]]]]])