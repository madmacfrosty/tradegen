(ns tradegen.date
  (:require [clj-time.coerce :as c]
            [clj-time.core :as t]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(defn- ->start-of-day
  [x]
  (let [tm (c/from-date x)
        [y m d] ((juxt t/year t/month t/day) tm)]
    (c/to-date (t/date-time y m d))))

(s/def ::date (s/with-gen
                inst?
                #(gen/fmap ->start-of-day (s/gen inst?))))

(s/def ::trade-date ::date)
(s/def ::settlement-date ::date)
(def trade-date-gen (s/gen ::trade-date))


(defn offset-by-days
  [date offset]
  (-> date
      c/from-date
      (t/plus (t/days offset))
      c/to-date))
