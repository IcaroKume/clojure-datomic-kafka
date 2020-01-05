(ns clojure-datomic-kafka.event-handler
  (:require [clojure-datomic-kafka.db :as db]))

(defn save-event [req]
  (ring.util.response/response (db/save-event (:body req)))
  )