(ns clojure-datomic-kafka.player-handler
  (:require [clojure-datomic-kafka.db :as db]))

(defn save-player [req]
  (ring.util.response/response (db/save-player (:body req))))

(defn update-player [req]
  (ring.util.response/response (-> (:body req)
                                   (assoc :id ((comp :id :params) req))
                                   (db/update-player))))

(defn find-all-players []
  (ring.util.response/response (db/find-all-players)))