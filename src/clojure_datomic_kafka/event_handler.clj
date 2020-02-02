(ns clojure-datomic-kafka.event-handler
  (:require [clojure-datomic-kafka.db :as db]
            [clojure-datomic-kafka.kafka :as cdk.kafka]))

(defn- on-event-saved
  [event]
  (cdk.kafka/produce event))

(defn save-event [req]
  (let [event (db/save-event (:body req))]
    (on-event-saved event)
    (ring.util.response/response event)))
