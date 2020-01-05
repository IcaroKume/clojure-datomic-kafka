(ns clojure-datomic-kafka.player-handler
  (:require [clojure-datomic-kafka.db :as db]))

(defn save-player [req]
  (ring.util.response/response (db/save-player (:body req)))
  )