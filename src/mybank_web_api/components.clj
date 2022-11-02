(ns mybank-web-api.components
  (:require [com.stuartsierra.component :as component]
            [mybank-web-api.server :as web-server]
            [mybank-web-api.database :as db]
            [mybank-web-api.config :as config]))

(def base
  (component/system-map
    :config (config/new-config)
    :database (component/using
                (db/new-database)
                [:config])
    :web-server (component/using
                  (web-server/new-server)
                  [:database :config]))
  )