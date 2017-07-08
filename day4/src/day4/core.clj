(ns day4.core
  (:require [clojure.string :as s]))

(def filename "resources/input.txt")

(defrecord Room [cipher-text ordered sector checksum])

(defn order-alphabets
  [cipher-text]
  (->>
    cipher-text
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

(defn rotate
  [number alphabet]
  (->
    alphabet
    int
    (+ number)
    (- 97)
    (mod 26)
    (+ 97)
    char))

(defn rotate-word
  [word number]
  (->>
    word
    (map #(if (= % \-) \space (rotate number %)))
    (apply str)))

(defn part1
  [rooms]
  (->>
    rooms
    (filter valid-checksum?)
    (map :sector)
    (apply +)))

(defn part2 
  [rooms]
  (->>
    rooms
    (filter valid-checksum?)
    (map (fn [{cipher-text :cipher-text sector :sector}]
           (vector
             (rotate-word cipher-text sector)
             sector)))
    (filter #(.contains (first %) "northpole"))
    first
    second))

(defn -main
  "Advent of Code '16 - Day 4
  Find the rooms using a silly Checksum and
  silly decryption"
  [& args]
  (let [rooms (parse-input filename)]
    (do
      (println "The answers for day 2 are:")
      (println "part 1:" (part1 rooms))
      (println "part 2:" (part2 rooms)))))

