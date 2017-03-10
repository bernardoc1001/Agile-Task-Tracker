(ns agile-task-tracker.proj-org
  (:require [reagent.core :as r]))


(defn create-org-pill
  [org-map]
  (let [org-id (:organisation-id org-map)
        org-name (:organisation-name org-map)]

    [:div {:class "proj-org-pill" :id org-id}
     [:a {:href (str "/project/" org-id)} org-name]]))

(defn create-proj-pill
  [proj-map]
  (let [proj-id (:project-id proj-map)
        proj-name (:project-name proj-map)]

    [:div {:class "proj-org-pill" :id proj-id}
     [:a {:href (str "/backlog/" proj-id)} proj-name]]))
