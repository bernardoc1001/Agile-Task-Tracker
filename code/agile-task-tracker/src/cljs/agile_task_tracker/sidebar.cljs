(ns agile-task-tracker.sidebar
  (:require [reagent.core :as r]
            [reagent.session :as session]))

(defn sidebar []
  [:div#sidebar-wrapper
	 [:a [:img {:src "/images/logo.png"}]]
	 [:ul.sidebar-nav
    (let [current-page-name (session/get :current-page-name)
          home-link [:li>a {:href "/"} "Home"]
          project-link [:li>a {:href (str "/project/" (session/get :organisation-id))} "Projects"]
          backlog-link [:li>a {:href (str "/backlog/" (session/get :project-id))} "Backlog"]
          current-sprint-link [:li>a {:href (str "/current-sprint/" (session/get :project-id))} "Current Sprint"]]
      
      (cond
        (= current-page-name "dashboard-page")
        home-link

        (= current-page-name "project-page")
        [:div
         home-link
         project-link]

        :else
        [:div
         home-link
         project-link
         backlog-link
         current-sprint-link]))]])

(defn menu-toggle-render []
  [:div.btn.btn-default "Toggle Menu"])

(defn menu-toggle-did-mount [this]
  (.click (js/$ (r/dom-node this))
          (fn [e]
            (.preventDefault e)
            (.toggleClass (js/$ "#wrapper") "toggled"))))


(defn menu-toggle []
	(r/create-class {:reagent-render menu-toggle-render
									 :component-did-mount menu-toggle-did-mount}))



