(ns agile-task-tracker.views.dashboard
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.common :as common]
            [ajax.core :refer [GET POST]]
            [goog.string :as gstring]
            [hipo.core :as hipo]
            [agile-task-tracker.proj-org :as proj-org]))

(defonce new-org
         (r/atom {:data {}}))

(defonce page-state
         (r/atom {:orgs []}))




(defn render-org
  [org-map col-id]
  (if (nil? (.getElementById js/document (:org-id org-map)))
    (let [org-pill (hipo/create (proj-org/create-org-pill org-map))]

      (.appendChild (.getElementById js/document col-id) org-pill))
    (.error js/console (str "Could not add org, already exists"))))


;---------------------ajax stuff----------------------------
;TODO refactor into common?

(defn handler
  [response]
  (.log js/console (str "handler response: " response)))

(defn error-handler
  [response]
  (.error js/console (str response)))

(defn get-org-by-id-handler
  [response]
  (.log js/console (str "get-task-by-id-handler response: " response))
  (render-org (get-in response [:_source]) "org-col"))

(defn get-org-by-id
  [org-id]
  (POST "/"
        {:params        {:data   {:org-id org-id}
                         :method "get-by-id"}
         :handler       get-org-by-id-handler
         :error-handler error-handler}))

(defn put-org-by-id-handler
  [response]
  (.log js/console (str "put-org-handler response: " response))
  ;task will be rendered in the get response handler
  (get-org-by-id (:_id response)))





;----------------------------------------------------------
(defn save-org-procedure []
  ;TODO make this single arity?
  (POST "/"
        {:params        {:data (:data @new-org)
                         :method "put-by-id"}
         :handler       put-org-by-id-handler
         :error-handler error-handler}))

(defn modal-org-creation-content []
  [:div
   [:div {:class "modal-header"}
    [:button {:type         "button"
              :class        "close"
              :data-dismiss "modal"
              :aria-label   "Close"}
     [:span {:aria-hidden "true"} (gstring/unescapeEntities "&times;")]]
    [:h4 {:class "modal-title"
          :id    "org-modal-title"}
     "Create an Organisation"]]
   [:div {:class "modal-body"}

    [:div [common/atom-input-field "Org-id: " new-org [:data :org-id]]]
    [:div [common/atom-input-field "Organisation name: " new-org [:data
                                                                 :org-name]]]


    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(save-org-procedure)}
      "Save"]]]])

(defn create-org-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [modal-org-creation-content]
                               {:show (reset! new-org {})})}
   "Create Organisation"])



(defn dashboard-page []
  [:div

   [:div#wrapper
    [sidebar/sidebar]

    [:div.page-content-wrapper>div.container-fluid>div.row>div.col-xs-12
     [sidebar/menu-toggle]
     [:p (str "page-state: " @page-state)]
     [:p (str "new-org: " @new-org)]
     [:p "org test"]
     [:div {:class "panel panel-default"}
      [:div {:class "panel-heading"} "Organisations"]
      [:div {:class "panel-body"}
       [:div {:class "row"}
        [:div {:class "col-sm-12"}
         [rmodals/modal-window]
         [create-org-button]
         [:div#org-col]]]]]]]])


