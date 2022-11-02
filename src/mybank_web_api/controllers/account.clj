(ns mybank-web-api.controllers.account
  (:require [mybank-web-api.logic.account :as logic.account])
  (:import (clojure.lang ExceptionInfo)))


(defn get-balance [context]
  (let [id-account  (-> context :request :path-params :id keyword)
        accounts    (-> context :accounts)]
    (assoc context :response {:status  200
                              :headers {"Content-Type" "text/plain"}
                              :body    (id-account @accounts "invalid account")})))

(defn deposit! [context]
  (try
    (let [id-account  (-> context :request :path-params :id keyword)
          amount      (-> context :request :body slurp parse-double)
          accounts    (-> context :accounts)]
      (when (logic.account/account-not-exist? accounts id-account)
        (throw (ex-info "Account does not exist" {})))
      (logic.account/deposit accounts id-account amount)
      (assoc context :response {:status  200
                                :headers {"Content-Type" "text/plain"}
                                :body    {:id-account id-account
                                          :balance    (id-account @accounts)}}))
    (catch ExceptionInfo e
      (assoc context :response {:status 404 :body (ex-message e) }))))

(defn withdraw! [context]
  (try
    (let [id-account  (-> context :request :path-params :id keyword)
          amount      (-> context :request :body slurp parse-double)
          accounts    (-> context :accounts)]
      (when (logic.account/account-not-exist? accounts id-account)
        (throw (ex-info "Account does not exist" {})))
      (when (logic.account/balance-not-negative? amount accounts id-account)
        (throw (ex-info "Cannot have a negative balance" {})))
      (logic.account/withdraw accounts id-account amount)
      (assoc context :response {:status  200
                                :headers {"Content-Type" "text/plain"}
                                :body    {:id-account id-account
                                          :balance    (id-account @accounts)}}))
    (catch ExceptionInfo e
      (assoc context :response {:status 404 :body (ex-message e)}))))
