(ns clojure-datomic-kafka.db
  (:require [datomic.api :as d]
            [clojure-datomic-kafka.utils :refer :all]))

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
    (assoc player :id (-> result :tempids (get "player")))))

(defn update-player [player]
  (do @(d/transact conn [{
                          :db/id       (:id player)
                          :player/name (:name player)
                          :player/life (:life player)}])
      player))

(def all-players '[:find ?e ?name ?life
                   :where [?e :player/name ?name]
                   [?e :player/life ?life]])

(defn to-player
  ([single-result]
   {:id   (get single-result 0)
    :name (get single-result 1)
    :life (get single-result 2)})
  ([single-result id]
   {:id   id
    :name (:player/name single-result)
    :life (:player/life single-result)}))

(defn to-players [q-result]
  (map to-player q-result))

(defn find-all-players []
  (let [db (d/db conn)]
    (-> (d/q all-players db)
        (to-players))))

(defn find-player [id]
  (let [db (d/db conn)]
    (-> (d/entity db id)
        (to-player id))))

(defn delete-player
  [id]
  (-> @(d/transact conn [{:db/excise id}])
      log-and-return))

(defn find-player-life-history [id]
  (let [db (d/history (d/db conn))]
    (->> (d/q '[:find ?life ?tx ?added
                :in $ ?e
                :where [?e :player/life ?life ?tx ?added]] db id)
         (map #(->> %
                    (map vector [:life :tx :added])
                    (into {})))
         (filter #(= (:added %) true))
         (sort-by :tx)
         (#(map :life %)))))

(defn save-event [event]
  (let [result @(d/transact conn [{
                                   :db/id        "event"
                                   :event/code   (:code event)
                                   :event/damage (:damage event)
                                   :event/player (:player event)
                                   }])]
    (assoc event :id (-> result :tempids (get "event")))))

(defn get-log
  []
  (-> conn
      d/log
      (d/tx-range nil nil)))
