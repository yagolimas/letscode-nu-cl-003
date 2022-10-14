(ns mybank-web-api.core
  (:import (clojure.lang ExceptionInfo))
  (:require [clojure.set :as set]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route])
  (:gen-class))

(defonce accounts (atom {:1 {:balance 100}
                         :2 {:balance 200}
                         :3 {:balance 300}}))

(defn get-balance [request]
  (let [id-account (-> request :path-params :id keyword)]
    {:status 200 :body (id-account @accounts "Invalid Account")}))

(defn make-deposit! [id-account amount]
  (let [_ (swap! accounts (fn [m] (update-in m [id-account :balance] #(+ % amount))))]
    {:status 200 :body {:id-account id-account
                        :balance    (:balance (id-account @accounts))}}))

(defn make-withdraw! [id-account amount]
  (let [_ (swap! accounts (fn [m] (update-in m [id-account :balance] #(- % amount))))]
    {:status 200 :body {:id-account id-account
                        :balance    (:balance (id-account @accounts))}}))

(defn account-not-exist-exception! [id-account accounts]
  (when (nil? (get @accounts id-account))
    (throw (ex-info "Account does not exist" {}))))

(defn not-negative-balance! [id-account accounts amount]
  (when (> amount (:balance (get @accounts id-account)))
    (throw (ex-info "Cannot have a negative balance" {}))))

(defn deposit! [request]
  (try
    (let [id-account (-> request :path-params :id keyword)
          amount (-> request :body slurp parse-double)]
      (account-not-exist-exception! id-account accounts)
      (make-deposit! id-account amount))
    (catch ExceptionInfo e
      {:status 404 :body (ex-message e)})))

(defn withdraw! [request]
  (try
    (let [id-account (-> request :path-params :id keyword)
          amount (-> request :body slurp parse-double)]
      (account-not-exist-exception! id-account accounts)
      (not-negative-balance! id-account accounts amount)
      (make-withdraw! id-account amount))
    (catch ExceptionInfo e
      {:status 404 :body (ex-message e)})))

(def get-balance-route
  #{["/saldo/:id"
     :get get-balance
     :route-name :get-balance]})

(def deposit-route
  #{["/deposito/:id"
     :post deposit!
     :route-name :deposit]})

(def withdraw-route
  #{["/saque/:id"
     :post withdraw!
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

(defn reset []
  (try
    (do
      (http/stop @server)
      (start))
    (catch Exception _ (start))))
