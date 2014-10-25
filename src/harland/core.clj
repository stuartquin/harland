(ns harland.core
  (:require  [harland.build :as build]
             [prone.middleware :as prone]
             [prone.debug :refer [debug]]
             [taoensso.timbre :as timbre]
             [compojure.handler :refer [site]]
             [compojure.core :refer [defroutes GET POST context]]
             [org.httpkit.server :refer [run-server]]))

(defn build-project [request]
  (debug)
  (build/schedule-build "harland"))

(defn status [request]
  "<p>Running</p>")

(defroutes all-routes
  (GET "/build/:project" [] build-project)
  (GET "/status" [] status))

(defn start-server
  [env]
  (run-server (cond-> (site #'all-routes)
                      (= :development env) prone/wrap-exceptions)
              {:port 8080}))

(defn -main [& args]
  (start-server :development)
  (timbre/info "Server started on 8080"))
