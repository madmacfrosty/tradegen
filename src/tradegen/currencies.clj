(ns warrant.currencies
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]))

(defn- convert-raw-ccys
  "Utility function to import currency definitions
   Takes a raw iso file (from http://www.xe.com/iso4217.php for example)
     AFN	Afghanistan Afghani
     ALL	Albania Lek
     AMD	Armenia Dram

  Spits an edn description to out"
  [in out]
  (let [lines (-> in io/resource slurp (string/split #"\n"))
        data (map #(->> %
                        (re-matches #"^([\S]+)[\s]+(.*)")
                        rest
                        (zipmap [:currency :description]))
                  lines)]
    (->> data
         pprint
         with-out-str
         (spit out))))

(def ^:private currencies (->> "iso-currencies.edn"
                               io/resource
                               slurp
                               edn/read-string
                               (map #(-> % :currency string/lower-case keyword))
                               (into #{})))

(s/def ::currency currencies)

(def pair-generator (gen/fmap
                     #(->> % (map name) (apply str) keyword)
                     (gen/such-that
                      (partial apply not=)
                      (gen/tuple (gen/elements currencies)
                                 (gen/elements currencies)))))

(s/def ::currency-pair (s/with-gen keyword? (constantly pair-generator)))

