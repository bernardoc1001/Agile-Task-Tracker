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
           (POST "/"  request (cond
                                (= "get-by-id" (get-in request [:params :method]))
                                (let [response (attes/get-doc-by-id "org-info" "org-info-mapping" (get-in request [:params :data :organisation-id]))]
                                  (if (= true (:found response))
                                    {:status 200 :body response}
                                    response))

                                (= "delete-by-id" (get-in request [:params :method]))
                                (let [response (attes/delete-doc-by-id
                                                 "org-info"
                                                 "org-info-mapping" (get-in request [:params :data :organisation-id]))]
                                  (if (= true (:found response))
                                    {:status 200 :body response}
                                    {:status 500 :body response}))

                                (= "query-by-term" (get-in request [:params :method]))
                                (let [response (attes/query-by-term "org-info" "org-info-mapping" (keyword "org-id") (get-in request [:params :data :organisation-id]))]
                                  (if (>= (get-in response [:hits :total]) 0)
                                    {:status 200 :body response}
                                    {:status 500 :body response}))

                                (= "put-by-id" (get-in request [:params :method]))
                                (let [response (attes/put-org-info (get-in request [:params :data]))]
                                  (if (= true (contains? response :created))
                                    {:status 200 :body response}
                                    response))))
           (GET "/project/:organisation-id" [organisation-id] (loading-page))
           (POST "/project" request (cond
                                      (= "get-by-id" (get-in request [:params :method]))
                                      (let [response (attes/get-doc-by-id "proj-info" "proj-info-mapping" (get-in request [:params :data :project-id]))]
                                        (if (= true (:found response))
                                          {:status 200 :body response}
                                          response))

                                      (= "delete-by-id" (get-in request [:params :method]))
                                      (let [response (attes/delete-doc-by-id "proj-info" "proj-info-mapping" (get-in request [:params :data :project-id]))]
                                        (if (= true (:found response))
                                          {:status 200 :body response}
                                          {:status 500 :body response}))

                                      (= "query-by-term" (get-in request [:params :method]))
                                      ;TODO does this work?
                                      (let [response (attes/query-by-term "proj-info" "proj-info-mapping" (keyword "organisation-id") (get-in request [:params :data :organisation-id]))]
                                        (if (>= (get-in response [:hits :total]) 0)
                                          {:status 200 :body response}
                                          {:status 500 :body response}))

                                      (= "put-by-id" (get-in request [:params :method]))
                                      (let [response (attes/put-proj-info (get-in request [:params :data]))]
                                        (if (= true (contains? response :created))
                                          {:status 200 :body response}
                                          response))))

           (GET "/current-sprint/:project-id" [project-id] (loading-page))
           (GET "/sprints/:project-id" [project-id] (loading-page))
           (GET "/backlog/:project-id" [project-id] (loading-page))
           (POST "/backlog" request (cond
                                      (= "get-by-id" (get-in request [:params :method]))
                                      (let [response (attes/get-doc-by-id "task-info" "task-info-mapping" (get-in request [:params :data :task-id]))]
                                        (if (= true (:found response))
                                          {:status 200 :body response}
                                          response))

                                      (= "delete-by-id" (get-in request [:params :method]))
                                      (let [response (attes/delete-doc-by-id "task-info" "task-info-mapping" (get-in request [:params :data :task-id]))]
                                        (if (= true (:found response))
                                          {:status 200 :body response}
                                          {:status 500 :body response}))

                                      (= "query-by-term" (get-in request [:params :method]))
                                      (let [response (attes/query-by-term "task-info" "task-info-mapping" (keyword "sprint-id") (get-in request [:params :data :sprint-id]))]
                                        (if (>= (get-in response [:hits :total]) 0)
                                          {:status 200 :body response}
                                          {:status 500 :body response}))

                                      (= "put-by-id" (get-in request [:params :method]))
                                      (let [response (attes/put-task-info (get-in request [:params :data]))]
                                        (if (= true (contains? response :created))
                                          {:status 200 :body response}
                                          response))

                                      (= "get-sprint-by-id" (get-in request [:params :method]))
                                      (let [response (attes/get-doc-by-id "sprint-info" "sprint-info-mapping" (get-in request [:params :data :sprint-id]))]
                                        (if (= true (:found response))
                                          {:status 200 :body response}
                                          response))

                                      (= "put-sprint-by-id" (get-in request [:params :method]))
                                      (let [response (attes/put-sprint-info (get-in request [:params :data]))]
                                        (if (= true (contains? response :created))
                                          {:status 200 :body response}
                                          response))

                                      ;TODO add this in for current sprint page
                                      (= "get-active-sprint" (get-in request [:params :method]))
                                      (let [response (attes/get-active-sprint "sprint-info" "sprint-info-mapping" (get-in request [:params :data :project-id]))
                                            num-of-hits (get-in response [:hits :total])]
                                        (if (= true (or (= 0 num-of-hits) (= 1 num-of-hits)))
                                          {:status 200 :body response}
                                          {:status 500 :body response}))))
           
           (resources "/")
           (not-found "Not Found, has it been included in both the handler.clj and core.cljs?")) ;TODO change not found message before demo

(def app (wrap-middleware #'routes))
