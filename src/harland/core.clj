(ns harland.core
  (:require [harland.messaging :as m]
            [prone.middleware :as prone]
            [prone.debug :refer [debug]]
            [taoensso.timbre :as timbre]
            [cheshire.core :refer [encode decode]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST context]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

; @TODO this should probably be POST/PUT?
(defn build-project
  "Schedules a build"
  [request]
  (let [project (:project (:params request))]
    (timbre/info "[POST] build-project" project)
    {:status 200
     :body (encode {:project project
                    :build (m/schedule-build project)})}))

(defn get-build
  "Gets the most recent build for a project"
  [request]
  (let [proj (:project (:params request))
        id (:id (:params request))]
  {:status 200
   :body (encode (m/get-build-info proj id))}))

(defn status [request]
  {:status 200
   :body (encode {:http true
                  :redis (m/status)})})

(defroutes all-routes
  (GET "/project/:project/build" [] get-build)
  (GET "/project/:project/build/:id" [] get-build)
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
