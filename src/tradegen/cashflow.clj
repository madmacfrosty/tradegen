(ns tradegen.cashflow
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [tradegen.currency :as ccy]
   [tradegen.primitive :as primitive]))

(s/def ::payer ::primitive/party)
(s/def ::receiver ::primitive/party)

(s/def ::cashflow (s/keys :req-un [::payer
                                   ::receiver
                                   ::ccy/currency
                                   ::primitive/amount]))

