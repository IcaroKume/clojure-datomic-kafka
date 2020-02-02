(ns clojure-datomic-kafka.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as json]
            [ring.adapter.jetty :as jetty]
            [clojure-datomic-kafka.db]
            [clojure-datomic-kafka.player-handler :refer :all]
            [clojure-datomic-kafka.event-handler :refer :all]
            [clojure-datomic-kafka.info :refer :all]
            [clojure-datomic-kafka.kafka :as kafka])
  )

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/datomic_log" _ (get-log))
           (GET "/players" _ (find-all-players))
           (POST "/players" req (save-player req))
           (PUT "/players/:id" req (update-player req))
           (DELETE "/players/:id" req (delete req))
           (GET "/players/:id" req (find-player req))
           (GET "/players/:id/life_history" req (find-player-life-history req))
           (POST "/events" req (save-event req))
           (route/not-found "Not Found"))

(kafka/setup-consumer on-event-saved)

(def app
  (-> (handler/api app-routes)
      (json/wrap-json-body {:keywords? true})
      (json/wrap-json-response)))

(defn -main []
  (jetty/run-jetty app {:port 5000 :join? false}))