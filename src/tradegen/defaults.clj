(ns tradegen.defaults
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [tradegen.amount :as amount]
   [tradegen.currency :as currency]
   [tradegen.date :as date]
   [tradegen.party :as party]
   [tradegen.tenor :as tenor]))

(defn generators []
  (gen/hash-map
   :parties party/party-tuple-gen
   :nominal amount/nominal-gen
   :quote currency/quote-gen
   :trade-date date/trade-date-gen
   :tenor tenor/tenor-gen))
