(ns tradegen.party
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::party string?)
(s/def ::payer ::party)
(s/def ::receiver ::party)

(def party-tuple-gen
  (gen/such-that
   (partial apply not=)
   (gen/tuple (s/gen ::party)
              (s/gen ::party))))
