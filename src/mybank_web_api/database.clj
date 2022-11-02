(ns mybank-web-api.database
  (:require [com.stuartsierra.component :as component]))

(defrecord Database [config]
  component/Lifecycle
  (start [this]
    (let [_ (println config)
          data-file (-> (-> config :config :db-file)
                        slurp
                        read-string)]
      (assoc this :accounts (atom data-file))))
  (stop [this]
    (assoc this :store nil)))

(defn new-database []
  (->Database {}))