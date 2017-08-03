(ns tradegen.fx.forward
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [tradegen.cashflow :as cashflow]
   [tradegen.currency :as currency]
   [tradegen.date :as date]
   [tradegen.primitive :as primitive]))

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
  [[[party1 party2] nominal quote trade-date t]]
  (let [{:keys [rate currency-pair]} quote
        [ccy1 ccy2] (-> currency-pair
                        currency/currency-pair->tuple
                        ccy-tuple->dealt-counter)
        settlement-date (date/offset-by-days trade-date t)]
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

(def t-gen (gen/elements (into #{} (range 3 1000 1))))

(defn fx-forward-gen
  ([] (fx-forward-gen nil))
  ([t] (gen/fmap ->forward
                 (gen/tuple
                  primitive/party-tuple-gen
                  primitive/nominal-gen
                  currency/quote-gen
                  (s/gen ::date/trade-date)
                  (if t (gen/return t) t-gen)))))
