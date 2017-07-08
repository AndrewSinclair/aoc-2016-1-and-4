(ns day4.core
  (:require [clojure.string :as s]))

(def filename "resources/input.txt")

(defrecord Room [cipher-text ordered sector checksum])

(defn order-alphabets
  [encryption]
  (->>
    encryption
    (filter #(Character/isLetter %))
    sort
    frequencies
    (sort-by second >)
    (map first)))

(defn destruct-line
  [line]
  (let [[_ encryption sector checksum]
       (re-matches #"(.*)-(\d+)\[(\w+)\]" line)]
    (->Room
      encryption
      (order-alphabets encryption)
      (Integer. sector)
      checksum)))

(defn parse-input
  [filename]
  (->>
    filename
    slurp
    s/split-lines
    (map destruct-line)))

(defn valid-checksum?
  [room]
  (let [encryption (take 5 (:ordered room))
        checksum   (:checksum room)]
    (->>
      (map vector encryption checksum)
      (every? (partial apply =)))))

(defn filter-real-rooms
  [rooms]
  (->>
    rooms
    (filter valid-checksum?)))

(defn part1 [] nil)
(defn part2 [] nil)

(defn -main
  "Advent of Code '16 - Day 4
  Find the rooms using a silly Checksum and
  silly decryption"
  [& args]
  (do
    (println "The answers for day 2 are:")
    (println "part 1:" (part1))
    (println "part 2:" (part2))))
