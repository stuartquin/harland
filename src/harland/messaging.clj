(ns harland.messaging
  (:require [me.raynes.conch :as sh]
            [clojure.core.async :as async]
            [taoensso.timbre :as timbre]
            [taoensso.carmine :as car :refer (wcar)]))

; TODO move this to conf file or something
(def master-redis (System/getenv "REDIS_URI"))
(def server-conn {:pool {} :spec {:uri master-redis}})
(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn schedule-build
  "Write to the build queue, should return a build id"
  [proj]
  (let [prefix (str "project:" proj)
        id (wcar* (car/incr (str prefix ":builds")))]
    (wcar* 
      (car/lpush "build:queue" (str proj "|" id)))
    id))

(defn update-build
  [proj id status out err]
  (let [prefix (str "project:" proj ":build:" id)]
    (wcar* 
      (car/set (str prefix ":status") status)
      (car/set (str prefix ":error") err)
      (car/set (str prefix ":out") out))))

(defn status
  "Ping redis connection"
  []
  (wcar* (car/ping)))

(defn poll-build-queue
  "Polls the build queue"
  []
  (let [chan (async/chan)]
    (async/thread
      (while true
        (timbre/info "Polling for builds")
        (let [result (wcar* (car/blpop "build:queue" 0))
              res (second result)]
          (println res)
          (when res
            (async/>!! chan (clojure.string/split res #"\|"))))))
      chan))
