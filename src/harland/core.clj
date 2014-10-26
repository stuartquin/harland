(ns harland.core
  (:require [harland.messaging :as m]
            [prone.middleware :as prone]
            [prone.debug :refer [debug]]
            [taoensso.timbre :as timbre]
            [cheshire.core :refer [encode decode]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST context]]
            [org.httpkit.server :refer [run-server]]))

; @TODO this should probably be POST/PUT?
(defn build-project
  "Schedules a build"
  [request]
  (let [project (:project (:params request))]
    (m/schedule-build project)
    {:status 200
     :body (encode {:project project
                    :build "SOME_ID"})}))

(defn get-build
  "Gets the most recent build for a project"
  [request]
  (let [project (:project (:params request))]
   ))

(defn status [request]
  "<p>Running</p>")

(defroutes all-routes
  (GET "/project/:project/build" [] get-build)
  (POST "/project/:project/build" [] build-project)
  (GET "/status" [] status))

(defn start-server
  [env]
  (run-server (cond-> (site #'all-routes)
                      (= :development env) prone/wrap-exceptions)
              {:port 8080}))

(defn -main [& args]
  (start-server :development)
  (timbre/info "Server started on 8080"))
