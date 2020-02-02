(ns clojure-datomic-kafka.utils)

(defn log-and-return
  [a]
  (do (println a) (identity a)))