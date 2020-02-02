(ns clojure-datomic-kafka.kafka
  (:require [cheshire.core :as json])
  (:import (org.testcontainers.containers KafkaContainer)
           (org.testcontainers.containers.wait.strategy Wait)
           [org.apache.kafka.clients.admin AdminClient AdminClientConfig NewTopic]
           org.apache.kafka.clients.consumer.KafkaConsumer
           [org.apache.kafka.clients.producer KafkaProducer ProducerRecord]
           [org.apache.kafka.common.serialization StringDeserializer StringSerializer]))

;;https://www.testcontainers.org/modules/kafka/
(def kafka
  (.waitingFor (new KafkaContainer) (Wait/forListeningPort)))

(.start kafka)

(def bootstrap-server (.getBootstrapServers kafka))
(def topic "event")

(defn- create-topic
  []
  (let [config {AdminClientConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-server}
        adminClient (AdminClient/create config)]
    (.createTopics adminClient [(NewTopic. topic 1 1)])))

(defn- build-consumer
  []
  (let [consumer-props
        {"bootstrap.servers",  bootstrap-server
         "group.id",           "example"
         "key.deserializer",   StringDeserializer
         "value.deserializer", StringDeserializer
         "auto.offset.reset",  "earliest"
         "enable.auto.commit", "true"}]

    (doto (KafkaConsumer. consumer-props)
      (.subscribe [topic]))))

(defn- build-producer
  []
  (let [producer-props {"value.serializer"  StringSerializer
                        "key.serializer"    StringSerializer
                        "bootstrap.servers" bootstrap-server}]
    (KafkaProducer. producer-props)))

(create-topic)
(def consumer (build-consumer))
(def producer (build-producer))

(defn- keep-consuming
  [f]
  (while true
    (let [records (.poll consumer 100)]
      (doseq [record records]
        (let [event (json/parse-string (.value record) true)]
          (try (-> event f println)
               (catch Exception ex
                 (println ex))))))
    (.commitAsync consumer)))

(defn setup-consumer
  [f]
  (future-call (partial keep-consuming f)))

(defn produce
  [event]
  (let [event-string (json/generate-string event)]
    (.send producer (ProducerRecord. topic event-string))))

