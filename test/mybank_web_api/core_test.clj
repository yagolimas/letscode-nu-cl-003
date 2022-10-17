(ns mybank-web-api.core-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as test-http]
            [io.pedestal.http :as http]
            [mybank-web-api.core :refer :all]))

(defn test-post [server verb url body]
  (test-http/response-for (::http/service-fn @server) verb url :body body))

(defn test-request [server verb url]
  (test-http/response-for (::http/service-fn @server) verb url))

(deftest get-balance-test
  (testing "Get balance by client is done successfully"
    (let [_         (start)
          response  (test-request server :get "/saldo/1")
          _         (http/stop @server)]
      (is (= "{:balance 100}" (:body response)))))

  (testing "Get all balances by client has status successfully"
    (are [route result] (= (-> (test-request server :get route) :status) result)
                        "/saldo/1" 200
                        "/saldo/2" 200
                        "/saldo/3" 200)))

(deftest deposit-amount-test
  (testing "Deposit by client is done successfully"
    (let [_         (start)
          response  (test-post server :post "/deposito/2" "1000")
          _         (http/stop @server)]
      (is (= "{:id-account :2, :balance 1200.0}" (:body response))))))

(deftest withdraw-amount-test

  (testing "Withdraw by client is done successfully"
    (let [_         (start)
          response  (test-post server :post "/saque/3" "100")
          _         (http/stop @server)]
      (is (= "{:id-account :3, :balance 200.0}" (:body response)))))

  (testing "Do not allow withdrawal to generate negative balance"
    (let [_         (start)
          response  (test-post server :post "/saque/1" "200")
          _         (http/stop @server)]
      (is (= {:status  404
              :body    "Cannot have a negative balance"
              :headers {"Content-Security-Policy"           "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"
                        "Content-Type"                      "text/plain"
                        "Strict-Transport-Security"         "max-age=31536000; includeSubdomains"
                        "X-Content-Type-Options"            "nosniff"
                        "X-Download-Options"                "noopen"
                        "X-Frame-Options"                   "DENY"
                        "X-Permitted-Cross-Domain-Policies" "none"
                        "X-XSS-Protection"                  "1; mode=block"}}
             response)))))

(comment
  (reset)
  (start)
  (http/stop @server)

  (test-request server :get "/saldo/1")
  (test-request server :get "/saldo/2")
  (test-request server :get "/saldo/3")
  (test-request server :get "/saldo/4")

  (test-post server :post "/deposito/2" "863.99")
  (test-post server :post "/deposito/20" "863.99")

  (test-post server :post "/saque/3" "100")
  (test-post server :post "/saque/30" "100")

  )