(ns mybank-web-api.components
  (:require [com.stuartsierra.component :as component]
            [mybank-web-api.server :as web-server]
            [mybank-web-api.database :as db]))

(def base
  (component/system-map
    :database (db/new-database)
    :web-server (component/using
                  (web-server/new-server)
                  [:database])))