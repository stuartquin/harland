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
      (timbre/error "Build failed" tag path)
      (ex-data e))))

; TODO no build history yet
(defn save-output
  "Pushes build output to redis"
  [project out]
  (wcar*
    (timbre/info "Saving build output")
    (if (:proc out)
      (do
        (car/set (str "build-result:" project) "FAILURE")
        (car/set (str "build-error:" project) (:err (:proc out)))
        (car/set (str "build-output:" project) (apply str (:out (:proc out)))))
      (do
        (car/set (str "build-result:" project) "SUCCESS")
        (car/set (str "build-error:" project) nil)
        (car/set (str "build-output:" project) out)))))

(defn poll
  "Polls the build queue"
  []
  (while true
    (timbre/info "Polling for builds")
    (let [result (wcar* (car/blpop "build-queue" 0))
          project (second result)]
      (when project
        (timbre/info "Init Build" project)
        (save-output project (build project  "."))))))
