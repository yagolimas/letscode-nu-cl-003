(ns mybank-web-api.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database []
  component/Lifecycle
  (start [this]
    (let [data-file (-> "resources/accounts.edn"
                        slurp
                        read-string)]
      (assoc this :accounts (atom data-file))))
  (stop [this]
    (assoc this :store nil)))

(defn new-database []
  (->Database))