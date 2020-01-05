(ns clojure-datomic-kafka.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
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
      (is (= (:body response) "Hello World"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
