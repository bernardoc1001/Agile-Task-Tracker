(ns agile-task-tracker.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [agile-task-tracker.elasticsearch :as attes]
            [hiccup.page :refer [include-js include-css html5]]
            [agile-task-tracker.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h3 "ClojureScript has not been compiled!"]
   [:p "please run "
    [:b "lein figwheel"]
    " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   ;; ===jQuery===
   (include-js "https://code.jquery.com/jquery-2.1.1.min.js")
   (include-css "http://code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.min.css")
   (include-js "http://code.jquery.com/ui/1.11.2/jquery-ui.min.js")

   ;; ===Bootstrap===
   (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css")
   (include-js "https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js")

   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))
   ;; ===SideBar===
   (include-css "/css/simple-sidebar.css")])


(defn loading-page []
  (html5
    (head)
    [:body #_{:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))


(defroutes routes
           (GET "/" [] (loading-page))
           (GET "/backlog" [] (loading-page))
           (POST "/backlog" request (cond
                                      (= true (get-in request [:params :lookup-task]))
                                      (let [response (attes/get-doc-by-id "task-info" "task-info-mapping" (get-in request [:params :task-id]))]
                                        (if (= true (:found response))
                                          {:status 200 :body response}
                                          response))

                                      :else
                                      (let [response (attes/put-task-info (:params request))]
                                        (if (= true (contains? response :created))
                                          {:status 200 :body response}
                                          response))))
           #_(ANY "/backlog" request (attes/get-doc-by-id "task-info" "task-info-mapping" (get-in request [:params :body :task-id])))
           (resources "/")
           (not-found "Not Found, has it been included in both the handler.clj and core.cljs?")) ;TODO change not found message before demo

(def app (wrap-middleware #'routes))
