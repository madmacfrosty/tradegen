(ns tradegen.cashflow
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [tradegen.amount :as amount]
   [tradegen.currency :as ccy]
   [tradegen.party :as party]))

(s/def ::cashflow (s/keys :req-un [::party/payer
                                   ::party/receiver
                                   ::ccy/currency
                                   ::amount/amount]))

