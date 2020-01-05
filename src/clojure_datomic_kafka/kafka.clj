(ns clojure-datomic-kafka.kafka
  (:import (org.testcontainers.containers KafkaContainer)
           (org.testcontainers.containers.wait.strategy Wait)))

;;https://www.testcontainers.org/modules/kafka/
(comment
  (def kafka
    (.waitingFor (new KafkaContainer) (Wait/forListeningPort)))

  (.start kafka)
)