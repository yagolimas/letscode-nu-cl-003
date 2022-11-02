(ns mybank-web-api.server
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test-http]
            [io.pedestal.interceptor :as i]
            [mybank-web-api.router :as router]))

(defonce server (atom nil))

(defn start [service-map]
  (reset! server (http/start (http/create-server service-map))))

(defn test-request [verb url]
  (test-http/response-for (::http/service-fn @server) verb url))

(defrecord Server [database config]
  component/Lifecycle

  (start [this]
    (println "Start server...")
    (let [db-interceptor {:name :db-interceptor
                          :enter (fn [context]
                                   (assoc context :accounts (:accounts database)))}
          service-map-base {::http/routes router/routes
                            ::http/port (-> config :config :port)
                            ::http/type :jetty
                            ::http/join? false}
          service-map (-> service-map-base
                          (http/default-interceptors)
                          (update ::http/interceptors conj (i/interceptor db-interceptor)))]
      (try
        (when @server
          (println "Trying to stop server")
          (http/stop @server))
        (catch Exception e
          (str "Error executing server start:" (.getMessage e)))
        (finally
          (start service-map)
          (println "Server is running at: http://localhost:" (-> config :config :port))))
      (assoc this :test-request test-request)))

  (stop [this]
    (println "Trying to stop server...")
    (http/stop @server)))

(defn new-server []
  (->Server {} {}))