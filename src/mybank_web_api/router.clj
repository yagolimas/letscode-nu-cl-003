(ns mybank-web-api.router
  (:require [clojure.set :as set]
            [io.pedestal.http.route :as route]
            [io.pedestal.interceptor :as i]
            [mybank-web-api.controllers.account :as controllers.account]))

(def get-balance-route
  #{["/saldo/:id"
     :get (i/interceptor {:name :get-balance
                          :enter controllers.account/get-balance})
     :route-name :get-balance]})

(def deposit-route
  #{["/deposito/:id"
     :post (i/interceptor {:name :deposit
                           :enter controllers.account/deposit!})
     :route-name :deposit]})

(def withdraw-route
  #{["/saque/:id"
     :post (i/interceptor {:name :withdraw
                           :enter controllers.account/withdraw!})
     :route-name :withdraw]})

(def routes
  (route/expand-routes
    (set/union
      get-balance-route
      deposit-route
      withdraw-route)))