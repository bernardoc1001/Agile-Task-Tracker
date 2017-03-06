(ns agile-task-tracker.proj-org
  (:require [reagent.core :as r]))

;TODO Possibly refactor into one method. Is this worth having its on cljs?
(defn create-org-pill [org-map]
  (let [org-id (:org-id org-map)
        org-name (:org-name org-map)]

    [:div {:class "proj-org-pill" :id org-id}
     [:a {:href (str "/project/" org-name)} org-name]]))

(defn create-proj-pill [proj-map]
  (let [proj-id (:proj-id proj-map)
        proj-name (:proj-name proj-map)]

    [:div {:class "proj-org-pill" :id proj-id}
     [:a {:href (str "/sprints/" proj-name)} proj-name]]))
