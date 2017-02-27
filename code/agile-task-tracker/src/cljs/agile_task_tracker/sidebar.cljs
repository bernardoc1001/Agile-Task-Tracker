(ns agile-task-tracker.sidebar
  (:require [reagent.core :as r]))

(defn sidebar []
  [:div#sidebar-wrapper
   [:ul.sidebar-nav
    [:li.sidebar-brand>a {:href "#"} "Agile Task Tracker"]
    [:li>a {:href "#"} "Home" ]
    [:li>a {:href "#"} "Project" ]
    [:li>a {:href "#"} "Backlog" ]]])

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



