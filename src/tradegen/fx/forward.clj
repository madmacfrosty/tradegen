(ns tradegen.fx.forward
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [tradegen.cashflow :as cashflow]
   [tradegen.currency :as currency]
   [tradegen.date :as date]
   [tradegen.defaults :as defaults]))

(s/def ::exchanged-currency1 ::cashflow/cashflow)
(s/def ::exchanged-currency2 ::cashflow/cashflow)

(s/def ::fx-forward (s/keys :req-un [::exchanged-currency1
                                     ::exchanged-currency2
                                     ::date/trade-date
                                     ::date/settlement-date
                                     ::currency/exchange-rate]
                            :opt-un [::currency/dealt-currency]))

(defn- ccy-tuple->dealt-counter
  [[ccy1 ccy2]]
  (if (> 0.5 (rand))
    [ccy1 ccy2]
    [ccy2 ccy1]))

(defn- ->forward
  [{:keys [parties nominal quote trade-date tenor]}]
  (let [[party1 party2] parties 
        {:keys [rate currency-pair]} quote
        [ccy1 ccy2] (-> currency-pair
                        currency/currency-pair->tuple
                        ccy-tuple->dealt-counter)
        settlement-date (date/offset-by-days trade-date tenor)]
    {:exchanged-currency1 {:currency ccy1
                           :amount nominal
                           :payer party1
                           :receiver party2}
     :exchanged-currency2 {:currency ccy2
                           :amount (currency/calculate-ccy2 ccy1 quote nominal)
                           :payer party2
                           :receiver party1}
     :exchange-rate quote
     :dealt-currency ccy1
     :trade-date trade-date
     :settlement-date settlement-date}))



(defn fx-forward-gen
  ([] (gen/fmap ->forward (defaults/generators))))
