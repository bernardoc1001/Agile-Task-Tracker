(ns agile-task-tracker.views.project
	(:require [reagent.core :as r]
						[reagent-modals.modals :as rmodals]
						[agile-task-tracker.common :as common]
						[ajax.core :refer [GET POST]]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
						[goog.string :as gstring]
						[agile-task-tracker.sidebar :as sidebar]
						[hipo.core :as hipo]
						[agile-task-tracker.proj-org :as proj-org]
						[reagent.session :as session]))


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
;TODO refactor into another file?
(defn get-proj-by-id-handler
	[response]
	(.log js/console (str "get-task-by-id-handler response: " response))
	(render-proj (get-in response [:_source]) "proj-col"))

(defn get-proj-by-id
	[proj-id]
	(POST (route-calculator)
				{:params        {:data   {:proj-id proj-id}
												 :method "get-by-id"}
				 :handler       get-proj-by-id-handler
				 :error-handler error-handler}))

(defn put-proj-by-id-handler
	[response]
	(.log js/console (str "put-proj-handler response: " response))
	(get-proj-by-id (:_id response)))

(defn put-proj-by-id
  [project-map]
  (POST (route-calculator)
        {:params        {:data project-map
                         :method "put-by-id"}
         :handler       put-proj-by-id-handler
         :error-handler error-handler}))

;----------------------------------------------------------


(defn modal-proj-creation-content []
	(swap! new-proj assoc-in [:data :org-id] (session/get :organisation-id))
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
		;TODO Form validation
		[:form
		 [:div {:class "form-group"}
			[:label {:for "proj-id-form"} "Project ID: "]
			[:input {:type "text", :class "form-control", :id "proj-id-form",
							 :placeholder "Enter Project ID" :on-change #(common/onclick-swap-atom! new-proj [:data :proj-id]%)}]
			[:small {:class "form-text text-muted"} "Required"]]

		 [:div {:class "form-group"}
			[:label {:for "proj-name-form"} "Project name: "]
			[:input {:type "text", :class "form-control", :name "proj-name-form",
							 :placeholder "Enter Project name" :on-change #(common/onclick-swap-atom! new-proj [:data :proj-name]%)}]
			[:small {:class "form-text text-muted"} "Required"]]

		 [:div {:class "form-group"}
			[:label {:for "man-hours-form"} "Man-hours: "]
			[:input {:type "text", :class "form-control", :id "man-hours-form",
							 :placeholder "Enter man-hours" :on-change #(common/onclick-swap-atom! new-proj [:data :man-hours]%)}]]

		 [:div {:class "form-group"}
			[:label {:for "start-date"} "Project Start-Date: "]
			[:input {:type "date", :class "form-control", :id "start-date" :on-change #(common/onclick-swap-atom! new-proj [:data :start-date] %)}]]

		 [:div {:class "form-group"}
			[:label {:for "end-date"} "Project end-Date: "]
			[:input {:type "date", :class "form-control", :id "end-date" :on-change #(common/onclick-swap-atom! new-proj [:data :end-date] %)}]]]
		
		[:div {:class "modal-footer"}
		 [:div.btn.btn-secondary {:type         "button"
															:data-dismiss "modal"}
			"Close"]
		 [:div.btn.btn-primary {:type         "button"
														:data-dismiss "modal"
														:on-click     #(put-proj-by-id (:data @new-proj))}
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