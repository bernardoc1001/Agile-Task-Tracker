(ns agile-task-tracker.proj-org
  (:require [reagent.core :as r]))


(defn create-org-pill [org-map]
  (let [org-id (:org-id org-map)
        org-name (:org-name org-map)]

    [:div {:class "org-pill" :id org-id} [:a {:href "/project"} org-name]]))
