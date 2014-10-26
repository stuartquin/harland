(ns harland.service
  (:require [me.raynes.conch :as sh]
            [harland.messaging :as m]
            [clojure.core.async :as async]
            [taoensso.timbre :as timbre]))

(defn build
  "Runs a build using conch"
  [proj]
  (try
    (let [t (str "--tag='" proj "'")]
      (sh/with-programs [docker]
        (docker "build" "--force-rm=true" t ".")))
    (catch Exception e
      (timbre/error "Build failed" proj)
      (ex-data e))))

; TODO no build history yet
(defn save-output
  "Pushes build output to redis"
  [proj id out]
  (timbre/info "Saving build output")
  (if (:proc out)
    (m/update-build proj id "FAILURE" (apply str (:out (:proc out))) (:err (:proc out)))
    (m/update-build proj id "SUCCESS" out nil)))

(defn start-service
  []
  (let [chan (m/poll-build-queue)]
    (while true
      (let [[proj id] (async/<!! chan)]
        (timbre/info "Build" proj id)
        (m/update-build proj id "RUNNING" nil nil)
        (save-output proj id (build proj))))))

(defn -main [& args]
  (start-service))
