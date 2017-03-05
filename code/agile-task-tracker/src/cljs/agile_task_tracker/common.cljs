(ns agile-task-tracker.common
  (:require [reagent.core :as r]))

(defn onclick-swap-atom!
  [state path value]
  (swap! state assoc-in
         path (-> value .-target .-value)))


(defn atom-input-field
  ([label type atom path]
   (if (= type "time")
     ;take in times as a number accurate to 3 decimal places
     [:label label [:input {:type      "number"
                            :step      "0.001"
                            :name      label
                            :on-change #(onclick-swap-atom! atom path %)}]]

     ;take in every other type (including non-time numbers , i.e priority-level)
     [:label label [:input {:type      type
                            :name      label
                            :on-change #(onclick-swap-atom! atom path %)}]]))
  ([label atom path]
   (atom-input-field label "text" atom path)))

