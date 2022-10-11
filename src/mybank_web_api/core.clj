(ns mybank-web-api.core
  (:import (clojure.lang ExceptionInfo))
  (:require [clojure.set :as set]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test-http])
  (:gen-class))

(defonce accounts (atom {:1 {:balance 100}
                         :2 {:balance 200}
                         :3 {:balance 300}}))

(defn get-balance [request]
  (let [id-account (-> request :path-params :id keyword)]
    {:status 200 :body {:balance (id-account @accounts "Invalid Account")}}))

(defn make-deposit! [id-account amount]
  (let [_ (swap! accounts (fn [m] (update-in m [id-account :balance] #(+ % amount))))]
    {:status 200 :body {:id-account id-account
                        :balance    (id-account @accounts)}}))

(defn make-withdraw! [id-account amount]
  (let [_ (swap! accounts (fn [m] (update-in m [id-account :balance] #(- % amount))))]
    {:status 200 :body {:id-account id-account
                        :balance    (id-account @accounts)}}))

(defn account-not-exist-exception! [id-account accounts]
  (when (nil? (get @accounts id-account))
    (throw (ex-info "Account does not exist" {}))))

(defn deposit [request]
  (try
    (let [id-account (-> request :path-params :id keyword)
          amount (-> request :body slurp parse-double)]
      (account-not-exist-exception! id-account accounts)
      (make-deposit! id-account amount))
    (catch ExceptionInfo e
      {:status 404 :body (ex-message e)})))

(defn withdraw [request]
  (try
    (let [id-account (-> request :path-params :id keyword)
          amount (-> request :body slurp parse-double)]
      (account-not-exist-exception! id-account accounts)
      (make-withdraw! id-account amount))
    (catch ExceptionInfo e
      {:status 404 :body (ex-message e)})))

(def get-balance-route
  #{["/saldo/:id"
     :get get-balance
     :route-name :get-balance]})

(def deposit-route
  #{["/deposito/:id"
     :post deposit
     :route-name :deposit]})

(def withdraw-route
  #{["/saque/:id"
     :post withdraw
     :route-name :withdraw]})

(def routes
  (route/expand-routes
    (set/union
      get-balance-route
      deposit-route
      withdraw-route)))

(defn create-server []
  (http/create-server
    {::http/routes routes
     ::http/type   :jetty
     ::http/port   8890
     ::http/join?  false}))

(defonce server (atom nil))

(defn start []
  (reset! server (http/start (create-server))))

(defn test-request [server verb url]
  (test-http/response-for (::http/service-fn @server) verb url))
(defn test-post [server verb url body]
  (test-http/response-for (::http/service-fn @server) verb url :body body))

(comment
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
