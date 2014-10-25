(ns harland.build
  (require [me.raynes.conch :as sh]
           [taoensso.carmine :as car :refer (wcar)]))

; TODO move this to conf file or something
(def master-redis "redis://localhost:6379")
(def server-conn {:pool {} :spec {:uri master-redis}})
(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn schedule-build
  "Schedule a build"
  [project-name]
  (wcar* 
    (car/lpush "build-queue" project-name)))

(defn build
  "Runs a build using conch"
  [tag path]
  (try
    (let [t (str "--tag='" tag "'")]
      (sh/with-programs [docker]
        (docker "build" "--force-rm=true" t path)))
    (catch Exception e
      (ex-data e))))
