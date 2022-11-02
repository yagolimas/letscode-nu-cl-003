(ns mybank-web-api.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [com.stuartsierra.component :as component]
            [mybank-web-api.components :as components]
            [clj-http.client :as client]))

(def sys (atom nil))
(defn start-test [] (reset! sys (component/start components/base)))
(defn stop-test [] (reset! sys (component/stop components/base)))

(deftest get-balance-test
  (testing "Get balance by client is done successfully"
    (let [_         (start-test)
          response  (client/get "http://localhost:9999/api/saldo/1")
          _         (stop-test)]
      (is (= "{:balance 100}" (:body response)))))

  ;(testing "Get all balances by client has status successfully"
  ;  (let [_         (main-test)
  ;        _ (are [route result] (= (-> (client/get sys :get route) :status) result)
  ;                            "/saldo/1" 200
  ;                            "/saldo/2" 200
  ;                            "/saldo/3" 200)
  ;        _ (component/stop components/base)]))
  )

(deftest deposit-amount-test
  (testing "Deposit by client is done successfully"
    (let [_         (start-test)
          response  (client/post "http://localhost:9999/api/deposito/1" {:body "199.90"})
          _         (stop-test)]
      (is (= "{:id-account :1, :balance {:balance 299.9}}" (:body response))))))

(deftest withdraw-amount-test

  (testing "Withdraw by client is done successfully"
    (let [_         (start-test)
          response  (client/post "http://localhost:9999/api/saque/2" {:body "50"})
          _         (stop-test)]
      (is (= "{:id-account :2, :balance {:balance 150.0}}" (:body response)))))

  ;(testing "Do not allow withdrawal to generate negative balance"
  ;  (let [_         (start-test)
  ;        response  (client/post "http://localhost:9999/saque/1" {:body "200"})
  ;        _ (println response)
  ;        _         (stop-test)]
  ;    (is (= "Cannot have a negative balance" (:body response)))))
  )