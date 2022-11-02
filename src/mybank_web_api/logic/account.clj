(ns mybank-web-api.logic.account)

(defn account-not-exist? [accounts id-account]
  (nil? (get @accounts id-account)))

(defn balance-not-negative? [amount accounts id-account]
  (> amount (:balance (get @accounts id-account))))

(defn deposit [accounts id-account amount]
  (swap! accounts (fn [m] (update-in m [id-account :balance] #(+ % amount)))))

(defn withdraw [accounts id-account amount]
  (swap! accounts (fn [m] (update-in m [id-account :balance] #(- % amount)))))