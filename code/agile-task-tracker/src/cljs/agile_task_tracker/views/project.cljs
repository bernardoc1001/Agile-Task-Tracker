(ns agile-task-tracker.views.project
	(:require [reagent.core :as r]
						[reagent-modals.modals :as rmodals]
						[agile-task-tracker.common :as common]
						[ajax.core :refer [GET POST]]
						[goog.string :as gstring]
						[agile-task-tracker.sidebar :as sidebar]
						[hipo.core :as hipo]
						[agile-task-tracker.proj-org :as proj-org]))


(defonce new-proj
				 (r/atom {:data {}}))

(defonce page-state
				 (r/atom {:projs []}))




(defn render-proj
	[proj-map col-id]
	(if (nil? (.getElementById js/document (:proj-id proj-map)))
		(let [proj-pill (hipo/create (proj-org/create-proj-pill proj-map))]

			(.appendChild (.getElementById js/document col-id) proj-pill))
		(.error js/console (str "Could not add proj, already exists"))))


;---------------------ajax stuff----------------------------
;TODO refactor into common?

(defn handler
	[response]
	(.log js/console (str "handler response: " response)))

(defn error-handler
	[response]
	(.error js/console (str response)))

(defn get-proj-by-id-handler
	[response]
	(.log js/console (str "get-task-by-id-handler response: " response))
	(render-proj (get-in response [:_source]) "proj-col"))

(defn get-proj-by-id
	[proj-id]
	(POST "/project"
				{:params        {:data   {:proj-id proj-id}
												 :method "get-by-id"}
				 :handler       get-proj-by-id-handler
				 :error-handler error-handler}))

(defn put-proj-by-id-handler
	[response]
	(.log js/console (str "put-proj-handler response: " response))
	(get-proj-by-id (:_id response)))





;----------------------------------------------------------
(defn save-proj-procedure []
	;TODO make this single arity?
	(POST "/project"
				{:params        {:data (:data @new-proj)
												 :method "put-by-id"}
				 :handler       put-proj-by-id-handler
				 :error-handler error-handler}))

(defn modal-proj-creation-content []
	[:div
	 [:div {:class "modal-header"}
		[:button {:type         "button"
							:class        "close"
							:data-dismiss "modal"
							:aria-label   "Close"}
		 [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
		[:h4 {:class "modal-title"
					:id    "proj-modal-title"}
		 "Create an Project"]]
	 [:div {:class "modal-body"}
		;TODO remove auto-generated/NA
		[:div [common/atom-input-field "proj-id: " new-proj [:data :proj-id]]]
		[:div [common/atom-input-field "Project name: " new-proj [:data :proj-name]]]
		[:div [common/atom-input-field "Org-id: " new-proj [:data :org-id]]]
		[:div [common/atom-input-field "Man-hours available: " new-proj [:data :man-hours]]]
		[:div [common/atom-input-field "Start-date: " new-proj [:data :start-date]]]
		[:div [common/atom-input-field "End-date: " new-proj [:data :end-date]]]



		[:div {:class "modal-footer"}
		 [:div.btn.btn-secondary {:type         "button"
															:data-dismiss "modal"}
			"Close"]
		 [:div.btn.btn-primary {:type         "button"
														:data-dismiss "modal"
														:on-click     #(save-proj-procedure)}
			"Save"]]]])

(defn create-proj-button []
	[:div.btn.btn-primary
	 {:on-click #(rmodals/modal! [modal-proj-creation-content]
															 {:show (reset! new-proj {})})}
	 "Create Project"])

(defn project-page []
	[:div

	 [:div#wrapper
		[sidebar/sidebar]

		[:div.page-content-wrapper>div.container-fluid>div.row>div.col-xs-12
		 [sidebar/menu-toggle]
		 [:p (str "page-state: " @page-state)]
		 [:p (str "new-proj: " @new-proj)]
		 [:div {:class "panel panel-default"}
			[:div {:class "panel-heading"} "Projects"]
			[:div {:class "panel-body"}
			 [:div {:class "row"}
				[:div {:class "col-sm-12"}
				 [:p ""]
				 [rmodals/modal-window]
				 [create-proj-button]
				 [:div#proj-col]]]]]]]])