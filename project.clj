(defproject harland "0.1.0-SNAPSHOT"
  :main harland.core
  :aot [harland.core harland.service]
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [http-kit "2.1.16"]
                 [compojure "1.2.1"]
                 [ring "1.2.1"]
                 [prone "0.6.0"]
                 [cheshire "5.3.1"]
                 [com.taoensso/carmine "2.7.0"]
                 [com.taoensso/timbre "3.3.1"]
                 [com.stuartsierra/component "0.2.2"]
                 [me.raynes/conch "0.8.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}})
