(ns tradegen.tenor
  (:require [clojure.spec.gen.alpha :as gen]))

(def tenor-gen (gen/elements (into #{} (range 3 1000 1))))
