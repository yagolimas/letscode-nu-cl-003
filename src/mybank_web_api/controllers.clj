(ns mybank-web-api.controllers
  (:import (clojure.lang ExceptionInfo)))

(defn account-not-exist-exception!
  [id-account accounts]
  (when (nil? (get @accounts id-account))
    (throw (ex-info "Account does not exist" {}))))

(defn not-negative-balance!
  [id-account accounts amount]
  (when (> amount (:balance (get @accounts id-account)))
    (throw (ex-info "Cannot have a negative balance" {}))))

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
      (account-not-exist-exception! id-account accounts)
      (swap! accounts (fn [m] (update-in m [id-account :balance] #(+ % amount))))
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
      (account-not-exist-exception! id-account accounts)
      (not-negative-balance! id-account accounts amount)
      (swap! accounts (fn [m] (update-in m [id-account :balance] #(- % amount))))
      (assoc context :response {:status  200
                                :headers {"Content-Type" "text/plain"}
                                :body    {:id-account id-account
                                          :balance    (id-account @accounts)}}))
    (catch ExceptionInfo e
      (assoc context :response {:status 404 :body (ex-message e)}))))
