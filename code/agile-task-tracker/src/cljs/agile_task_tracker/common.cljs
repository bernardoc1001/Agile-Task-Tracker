(ns agile-task-tracker.common
  (:require [reagent.core :as r]))

(defn onclick-swap-atom!
  [state path value]
  (swap! state assoc-in
         path (-> value .-target .-value)))