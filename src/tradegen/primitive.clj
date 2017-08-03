(ns tradegen.primitive
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::party string?)
(s/def ::amount double?)
(s/def ::payer ::party)
(s/def ::receiver ::party)

(def nominal-gen (gen/elements (into #{} (range 10000 10000000 10000))))

(def party-tuple-gen
  (gen/such-that
   (partial apply not=)
   (gen/tuple (s/gen ::party)
              (s/gen ::party))))
