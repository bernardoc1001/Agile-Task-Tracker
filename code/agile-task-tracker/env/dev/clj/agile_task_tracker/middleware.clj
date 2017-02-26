(ns agile-task-tracker.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-restful-format)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))  ;TODO look into enabling anti-forgery
      wrap-exceptions
      wrap-reload))
