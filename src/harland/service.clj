(ns harland.service
  (require [me.raynes.conch :as sh]
           [taoensso.timbre :as timbre]
           [taoensso.carmine :as car :refer (wcar)]))

; TODO move this to conf file or something
(def master-redis "redis://localhost:6379")
(def server-conn {:pool {} :spec {:uri master-redis}})
(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn build
  "Runs a build using conch"
  [tag path]
  (try
    (let [t (str "--tag='" tag "'")]
      (sh/with-programs [docker]
        (docker "build" "--force-rm=true" t path)))
    (catch Exception e
      (ex-data e))))

; TODO no build history yet
(defn save-output
  "Pushes build output to redis"
  [project output]
  (wcar*
    (timbre/info "Saving build output")
    (car/lset (str "build-output:" project) output)))

(defn poll
  "Polls the build queue"
  []
  (wcar*
    (while true
      (timbre/info "Polling for builds")
      (let [project (car/blpop "build-queue")]
        (when project
          (timbre/info "Init Build" project)
          (save-output project (build project  ".")))))))
