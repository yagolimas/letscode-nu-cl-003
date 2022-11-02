(ns mybank-web-api.core
  (:require [com.stuartsierra.component :as component]
            [mybank-web-api.components :as components]
            [clj-http.client :as client])
  (:gen-class))

(def sys (atom nil))

(defn main []
  (reset! sys (component/start components/base)))

(comment
  (main)
  (client/get "http://localhost:9999/api/saldo/1")
  (client/post "http://localhost:9999/api/deposito/1" {:body "199.93"})
  (client/post "http://localhost:9999/api/saque/1" {:body "50"})
  (component/stop components/base)

  ; curl -d "199.99" -X POST http://localhost:9999/api/deposito/1
  )


