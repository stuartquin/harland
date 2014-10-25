(ns harland.build
  (require [me.raynes.conch :as sh]
           [taoensso.carmine :as car :refer (wcar)]))

; TODO move this to conf file or something
(def master-redis "redis://localhost:6379")
(def server-conn {:pool {} :spec {:uri master-redis}})
(defmacro wcar* [& body] `(car/wcar server-conn ~@body))

(defn schedule-build
  "Write to the build queue"
  [project-name]
  (wcar* 
    (car/lpush "build-queue" project-name)))
