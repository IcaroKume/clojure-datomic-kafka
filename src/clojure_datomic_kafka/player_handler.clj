(ns clojure-datomic-kafka.player-handler
  (:require [clojure-datomic-kafka.db :as db]))

(defn save-player [req]
  (ring.util.response/response (db/save-player (:body req))))

(defn update-player [req]
  (ring.util.response/response (-> (:body req)
                                   (assoc :id (Long/valueOf ((comp :id :params) req)))
                                   (db/update-player))))

(defn find-player [req]
  (ring.util.response/response (-> ((comp :id :params) req)
                                   (Long/valueOf)
                                   (db/find-player))))

(defn find-all-players []
  (ring.util.response/response (db/find-all-players)))