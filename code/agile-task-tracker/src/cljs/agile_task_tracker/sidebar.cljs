(ns agile-task-tracker.sidebar
  (:require [reagent.core :as r]))

(defn sidebar []
  [:div#sidebar-wrapper
	 [:a [:img {:src "/images/logo.png"}]]
	 [:ul.sidebar-nav

    [:li>a {:href "/"} "Home" ]
    [:li>a {:href "/project"} "Projects" ]
    [:li>a {:href "/backlog"} "Backlog" ]
		[:li>a {:href "/sprints"} "Sprints"]]])

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



