(ns agile-task-tracker.views.dashboard
  (:require [reagent.core :as r]
            [reagent-modals.modals :as rmodals]
            [agile-task-tracker.sidebar :as sidebar]
            [agile-task-tracker.common :as common]
            [ajax.core :refer [GET POST]]
            [agile-task-tracker.ajax :refer [handler error-handler route-calculator]]
            [goog.string :as gstring]
            [hipo.core :as hipo]
            [agile-task-tracker.proj-org :as proj-org]
            [clojure.string :as string]))

(defonce new-org
         (r/atom {:data {}}))

(defonce page-state
         (r/atom {:orgs []}))




(defn render-org
  [org-map col-id]
  (if (nil? (.getElementById js/document (:organisation-id org-map)))
    (let [org-pill (hipo/create (proj-org/create-org-pill org-map))]

      (.appendChild (.getElementById js/document col-id) org-pill))
    (.error js/console (str "Could not add org, already exists"))))


;---------------------ajax stuff----------------------------
;TODO refactor into another file?
(defn get-org-by-id-handler
  [response]
  (.log js/console (str "get-task-by-id-handler response: " response))
  (render-org (get-in response [:_source]) "org-col"))

(defn get-org-by-id
  [organisation-id]
  (POST (route-calculator)
        {:params        {:data   {:organisation-id organisation-id}
                         :method "get-by-id"}
         :handler       get-org-by-id-handler
         :error-handler error-handler}))

(defn put-org-by-id-handler
  [response]
  (.log js/console (str "put-org-handler response: " response))
  ;task will be rendered in the get response handler
  (get-org-by-id (:_id response)))


(defn put-org-by-id
  [org-map]
  (POST (route-calculator)
        {:params        {:data org-map
                         :method "put-by-id"}
         :handler       put-org-by-id-handler
         :error-handler error-handler}))

(defn get-all-orgs-handler
  [response]
  (.log js/console (str "get-all-orgs-handler response: " response))
  (let [hits-vector (get-in response [:hits :hits])]
    (doseq [hit hits-vector]
      (render-org (:_source hit) "org-col"))))

(defn get-all-orgs []
  (POST (route-calculator)
        {:params {:method "get-all-from-index"}
         :handler get-all-orgs-handler
         :error-handler error-handler}))

(defn load-orgs []
  (get-all-orgs))

(defn refresh-organisations-button []
  [:div.btn.btn-primary
   {:on-click #(load-orgs)}
   "Refresh Organisations"])

(defn organisation-id-contains-white-space [organisation-map]
  (boolean (re-find #" " (:organisation-id organisation-map))))

(defn validate-organisation
  "Checks organisation for required info, returns true if correct."
  [organisation-map]
  (let  [oid-blank? (string/blank? (:organisation-id organisation-map))
         name-blank? (string/blank? (:organisation-name organisation-map))]
    (and (not oid-blank?) (not name-blank?))))

(defn save-organisation-procedure
  "Posts organisation info if true, alerts user if false"
  [organisation-map]
  (if (validate-organisation organisation-map)
    (if (organisation-id-contains-white-space organisation-map)
      (js/alert "Please remove whitespace from id")
      (put-org-by-id organisation-map))
    (js/alert "Please fill out required details")))

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

    [:form
     [:div {:class "form-group"}
      [:label {:for "org-id-form"} "Organisation ID: "]
      [:input {:type "text", :class "form-control", :id "org-id-form",
               :placeholder "Enter Organisation ID" :on-change #(common/onclick-swap-atom! new-org [:data :organisation-id] %)}]
      [:small {:class "form-text text-muted"} "Required"]]

     [:div {:class "form-group"}
      [:label {:for "org-name-form"} "Organisation name: "]
      [:input {:type "text", :class "form-control", :id "org-id-form",
               :placeholder "Enter Organisation Name" :on-change #(common/onclick-swap-atom! new-org [:data :organisation-name]%)}]
      [:small {:class "form-text text-muted"} "Required"]]
     ]
    [:div {:class "modal-footer"}
     [:div.btn.btn-secondary {:type         "button"
                              :data-dismiss "modal"}
      "Close"]
     [:div.btn.btn-primary {:type         "button"
                            :data-dismiss "modal"
                            :on-click     #(save-organisation-procedure (:data @new-org))}
      "Save"]]]])

(defn create-org-button []
  [:div.btn.btn-primary
   {:on-click #(rmodals/modal! [modal-org-creation-content]
                               {:show (reset! new-org {})})}
   "Create Organisation"])



(defn dashboard-page []
  (load-orgs)
  [:div
   [:div#wrapper
    [sidebar/sidebar]

    [:div.page-content-wrapper>div.container-fluid>div.row>div.col-xs-12
     [sidebar/menu-toggle]
     [:p (str "page-state: " @page-state)]
     [:p (str "new-org: " @new-org)]
     [:div {:class "panel panel-default"}
      [:div {:class "panel-heading"} "Organisations"]
      [:div {:class "panel-body"}
       [:div {:class "row"}
        [:div {:class "col-sm-12"}
         [rmodals/modal-window]
         [create-org-button]
         [refresh-organisations-button]
         [:div#org-col]]]]]]]])


