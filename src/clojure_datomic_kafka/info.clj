(ns clojure-datomic-kafka.info
  (:require [clojure-datomic-kafka.db :as cdk.db]))

(defn datum->map
  [datum]
  {:a (.a datum)
   :e (.e datum)
   :v (.v datum)
   :tx (.tx datum)})

(defn get-log []
  (let [log (cdk.db/get-log)
        [schema data] (mapv :data log)]
    (ring.util.response/response {:schema (mapv datum->map schema)
                                  :data (mapv datum->map data)})))
