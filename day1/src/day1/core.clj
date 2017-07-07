(ns day1.core
  (:require [clojure.string :as s]))

(def filename "resources/input.txt")

(defn get-input
  [filename]
  (-> filename
      slurp
      s/trim
      (s/split #", ")))

(defn day1
  []
  nil)

(defn -main
  "Advent of Code '16 - Day 1
  How far away is the HQ?"
  [& args]
  (do
    (println "The answers for day 1 are:")
    (println "part 1:" (day1))))

