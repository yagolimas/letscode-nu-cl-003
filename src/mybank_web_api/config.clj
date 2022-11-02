(ns mybank-web-api.config
  (:require [com.stuartsierra.component :as component]))

(defrecord Config []
  component/Lifecycle
  (start [this]
    (let [config-file (-> "resources/config.edn"
                          slurp
                          read-string)]
      (assoc this :config config-file)))

  (stop [this]
    (dissoc this :config)))

(defn new-config []
  (->Config))