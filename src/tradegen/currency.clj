(ns tradegen.currency
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
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

(def ^:private  majors #{[:eur :gbp]
                         [:eur :usd]
                         [:eur :jpy]
                         [:eur :chf]
                         [:gbp :usd]
                         [:gbp :chf]
                         [:gbp :jpy]
                         [:usd :chf]
                         [:usd :jpy]
                         [:chf :jpy]})

(def ^:private commodities #{[:eur :aud]
                             [:eur :cad]
                             [:eur :nzd]
                             [:gbp :aud]
                             [:gbp :cad]
                             [:gbp :nzd]
                             [:aud :cad]
                             [:aud :chf]
                             [:aud :jpy]
                             [:aud :nzd]
                             [:aud :usd]
                             [:nzd :cad]
                             [:nzd :chf]
                             [:nzd :jpy]
                             [:nzd :usd]
                             [:cad :chf]
                             [:cad :jpy]})

(def ^:private rates
  {#{:usd} 1
   #{:eur :usd} 1.18
   #{:gbp :usd} 1.32
   #{:cad :usd} 1.259
   #{:nzd :usd} 0.742
   #{:chf :usd} 0.968
   #{:jpy :usd} 110.16
   #{:aud :usd} 0.795})

(defn- ccy->usd
  [c]
  (let [pair (into #{} [c :usd])
        rate (get rates pair (rand))]
    (if (#{:gbp :eur :aud :nzd} c)
      rate
      (/ 1.0 rate))))

(defn- calculate-rate
  [x]
  (let [[ccy1 ccy2] x]
    (/ (ccy->usd ccy1)
       (ccy->usd ccy2))))

(defn- ->currency-pair
  [[ccy1 ccy2]]
  (keyword (str (name ccy1)
                (name ccy2))))

(s/def ::currency currencies)
(s/def ::rate double?)
(s/def ::exchange-rate (s/keys :req-un [::currency-pair ::rate]))

(def major-pair-gen (gen/elements majors))
(def pair-gen (gen/elements (set/union majors commodities)))
(def quote-gen (gen/fmap #(hash-map :rate (calculate-rate %)
                                    :currency-pair (->currency-pair %))
                         pair-gen))

(defn currency-pair->tuple
  [pair]
  (let [n (name pair)
        x (subs n 0 3)
        y (subs n 3)]
    (mapv keyword [x y])))

(defn calculate-ccy2
  [ccy1 {:keys [rate currency-pair]} amount]
  (let [[base counter] (currency-pair->tuple currency-pair)]
    (if (= ccy1 base)
      (* rate amount)
      (/ amount rate))))
