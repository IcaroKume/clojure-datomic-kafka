(ns clojure-datomic-kafka.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as json]
            [ring.adapter.jetty :as jetty]
            [clojure-datomic-kafka.db]
            [clojure-datomic-kafka.player-handler :refer :all]
            [clojure-datomic-kafka.event-handler :refer :all]
            [clojure-datomic-kafka.kafka])
  )

(defroutes app-routes
           (GET "/" [] "Hello World")
           (POST "/players" req (save-player req))
           (POST "/events" req (save-event req))
           (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (json/wrap-json-body {:keywords? true})
      (json/wrap-json-response)))

(defn -main []
  (jetty/run-jetty app {:port 5000 :join? false}))