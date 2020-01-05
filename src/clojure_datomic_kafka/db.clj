(ns clojure-datomic-kafka.db
  (:require [datomic.api :as d]))

;; https://docs.datomic.com/on-prem/peer-getting-started.html#connecting
(d/create-database "datomic:mem://events")

(def conn
  (d/connect "datomic:mem://events"))

(def schema [
             ;; player
             {:db/ident       :player/name
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "player name"}
             {:db/ident       :player/life
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "player life"}
             ;; event
             {:db/ident       :event/code
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "uuid for the event"}
             {:db/ident       :event/damage
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "event damage"}
             {:db/ident       :event/player
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "event player who will receive damage"}
             ])

(d/transact conn schema)

(defn save-player [player]
  (let [result @(d/transact conn [{
                                   :db/id       "player"
                                   :player/name (:name player)
                                   :player/life (:life player)
                                   }])]
    (assoc player :id (-> result :tempids (get "player")))
    )
  )

(def all-players '[:find ?e ?name ?life
                   :where [?e :player/name ?name]
                   [?e :player/life ?life]])

(defn to-player [single-result]
  {
   :id   (get single-result 0)
   :name (get single-result 1)
   :life (get single-result 2)
   })

(defn to-players [q-result]
  (map to-player q-result))

(defn find-all-players []
  (let [db (d/db conn)]
    (-> (d/q all-players db)
        (to-players))))

(defn save-event [event]
  (let [result @(d/transact conn [{
                                   :db/id        "event"
                                   :event/code   (:code event)
                                   :event/damage (:damage event)
                                   :event/player (:player event)
                                   }])]
    (assoc event :id (-> result :tempids (get "event")))
    ))
