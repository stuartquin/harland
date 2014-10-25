(ns harland.build
  (require [me.raynes.conch :as sh]))

(defn build
  "Runs a build using conch"
  [tag path]
  (try
    (let [t (str "--tag='" tag "'")]
      (sh/with-programs [docker]
        (docker "build" "--force-rm=true" t path)))
    (catch Exception e
      (println "Something blew")
      (println (ex-data e)))))
