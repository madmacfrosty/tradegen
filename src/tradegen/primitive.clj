(ns warrant.primitive
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [warrant.currencies :as ccy]))

(s/def ::party string?)
(s/def ::trade-date inst?)
(s/def ::settlement-date inst?)
(s/def ::party string?)
(s/def ::rate double?)
(s/def ::amount double?)

(s/def ::payer ::party)
(s/def ::receiver ::party)
(s/def ::cashflow (s/keys :req-un [::payer
                                   ::receiver
                                   ::ccy/currency
                                   ::amount]))

(s/def ::exchanged-currency1 ::cashflow)
(s/def ::exchanged-currency2 ::cashflow)
(s/def ::dealt-currency ::ccy/currency)


(s/def ::exchange-rate (s/keys :req-un [::ccy/currency-pair ::rate]))

(s/def ::fx-spot (s/keys :req-un [::exchanged-currency1
                                  ::exchanged-currency2
                                  ::trade-date
                                  ::settlement-date
                                  ::exchange-rate]
                         :opt-un [::dealt-currency]))

;; Generate a tuple of
;; currency pair + rate + settlement offset (T+2) + party a + party b + trade-date
;; pass into bind to calculate sensible values
