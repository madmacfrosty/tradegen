(ns tradegen.amount
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]))

(s/def ::amount double?)
  
(def nominal-gen (gen/elements (into #{} (range 10000 10000000 10000))))
