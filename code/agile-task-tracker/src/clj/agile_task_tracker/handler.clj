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
	 [:title "Agile Task Tracker"]
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
					 (GET "/project" [] (loading-page))
					 (GET "/sprints" [] (loading-page))
           (GET "/backlog" [] (loading-page))
           (POST "/backlog" request (cond
                                      (= "get-by-id" (get-in request [:params :method]))
                                      (let [response (attes/get-doc-by-id "task-info" "task-info-mapping" (get-in request [:params :data :task-id]))]
                                        (if (= true (:found response)) ;TODO make status checker functions and import from elasticsearch.clj
                                          {:status 200 :body response}
                                          response))

                                      (= "delete-by-id" (get-in request [:params :method]))
                                      (let [response (attes/delete-doc-by-id "task-info" "task-info-mapping" (get-in request [:params :data :task-id]))]
                                        (if (= true (:found response)) ;TODO make status checker functions and import from elasticsearch.clj
                                          {:status 200 :body response}
                                          {:status 500 :body response}))

                                      (= "query-by-term" (get-in request [:params :method]))
                                      (let [response (attes/query-by-term "task-info" "task-info-mapping" (keyword "sprint-id") (get-in request [:params :data :sprint-id]))]
                                        (if (>= (get-in response [:hits :total]) 0)
                                          {:status 200 :body response}
                                          {:status 500 :body response}))

                                      :else
                                      (let [response (attes/put-task-info (:params request))]
                                        (if (= true (contains? response :created)) ;TODO make status checker functions and import from elasticsearch.clj
                                          {:status 200 :body response}
                                          response))))


           (resources "/")
           (not-found "Not Found, has it been included in both the handler.clj and core.cljs?")) ;TODO change not found message before demo

(def app (wrap-middleware #'routes))
