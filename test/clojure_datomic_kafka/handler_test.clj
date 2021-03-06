(ns clojure-datomic-kafka.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [clojure-datomic-kafka.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))

  (testing "save player"
    (let [response (app (-> (mock/request :post "/players")
                            (mock/json-body {:name "jj"
                                             :life 20})))]
      (is (= (:status response) 200))
      (let [body (json/parse-string (:body response) true)]
        (is (= (:name body) "jj"))
        (is (= (:life body) 20))
        (not (nil? (:id body))))))

  ;; TODO 17592186045418 because is the first entity
  (testing "update player"
    (let [response (app (-> (mock/request :put "/players/17592186045418")
                            (mock/json-body {:name "kk"
                                             :life 30})))]
      (is (= (:status response) 200))
      (let [body (json/parse-string (:body response) true)]
        (is (= (:name body) "kk"))
        (is (= (:life body) 30))
        (not (nil? (:id body))))))

  ;; TODO reset database after each test?
  (testing "get players"
    (let [response (app (mock/request :get "/players"))]
      (is (= (:status response) 200))
      (let [body (json/parse-string (:body response) true)]
        (is (= (str (type body)) "class clojure.lang.LazySeq"))
        (is (= (count body) 1))
        (is (= (:name (first body)) "kk")))))

  ;; TODO 17592186045418 because is the first entity
  (testing "get player"
    (let [response (app (mock/request :get "/players/17592186045418"))]
      (is (= (:status response) 200))
      (let [body (json/parse-string (:body response) true)]
        (is (= (:name body) "kk")))))

  ;; TODO 17592186045418 because is the first entity
  (testing "get player history"
    (let [response (app (mock/request :get "/players/17592186045418/life_history"))]
      (is (= (:status response) 200))
      (let [body (json/parse-string (:body response) true)]
        (is (= (str (type body)) "class clojure.lang.LazySeq"))
        (is (= body [20 30])))))

  (testing "save events"
    (let [response (app (-> (mock/request :post "/events")
                            (mock/json-body {:code   "jj"
                                             :damage 20
                                             :player 100})))]
      (is (= (:status response) 200))
      (let [body (json/parse-string (:body response) true)]
        (is (= (:code body) "jj"))
        (is (= (:damage body) 20))
        (is (= (:player body) 100))
        (not (nil? (:id body))))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
