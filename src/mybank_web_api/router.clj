(ns mybank-web-api.router
  (:require [mybank-web-api.controllers :as controller]
            [clojure.set :as set]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as i]))

(def get-balance-route
  #{["/saldo/:id"
     :get (i/interceptor {:name :get-balance
                          :enter controller/get-balance})
     :route-name :get-balance]})

(def deposit-route
  #{["/deposito/:id"
     :post (i/interceptor {:name :deposit
                           :enter controller/deposit!})
     :route-name :deposit]})

(def withdraw-route
  #{["/saque/:id"
     :post (i/interceptor {:name :withdraw
                           :enter controller/withdraw!})
     :route-name :withdraw]})

(def routes
  (route/expand-routes
    (set/union
      get-balance-route
      deposit-route
      withdraw-route)))