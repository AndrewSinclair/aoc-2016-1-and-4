(ns day1.core
  (:require [clojure.string :as s]))

(def filename "resources/input.txt")

(defrecord Instruction [direction distance])
(defrecord Position    [x y])
(defrecord State       [heading position])

(def initial-state
  (->State
    :north
    (->Position 0 0)))

(defn read-inputs
  [filename]
  (-> filename
      slurp
      s/trim
      (s/split #", ")))

(defn str->instruction
  [instr]
  (let [direction (first instr)
        distance  (->>
                    instr
                    rest
                    (apply str)
                    Integer.)]
    (->Instruction direction distance)))

(defn inputs->instructions
  [inputs]
  (map str->instruction inputs))

(defn calc-next-heading
  [heading direction]
  (if (= direction \R)
    (case heading
      :north :east
      :east  :south
      :south :west
      :west  :north)
    (case heading
      :north :west
      :east  :north
      :south :east
      :west  :south)))

(defn calc-next-position
  [heading {x :x y :y} distance]
  (case heading
    :north (->Position x              (+ y distance))
    :south (->Position x              (- y distance))
    :east  (->Position (+ x distance) y)
    :west  (->Position (- x distance) y)))

(defn calc-next-state
  [{heading   :heading
    position  :position}
   {direction :direction
    distance  :distance}]
  (let [next-heading
         (calc-next-heading
           heading
           direction)
        next-position
         (calc-next-position
           next-heading
           position
           distance)]
   (->State next-heading next-position)))

(defn calc-positions-iteratively
  [heading position distance]
  (->>
    (range 1 (inc distance))
    (map #(calc-next-position heading position %))))

(defn calc-states-iteratively
  [{heading   :heading
    position  :position}
   {direction :direction
    distance  :distance}]
  (let [next-heading   (calc-next-heading heading direction)
        next-positions (calc-positions-iteratively
                         next-heading
                         position
                         distance)]
    (map #(->State next-heading %) next-positions)))

(defn calc-all-intermediate-states
  [states-accumulated instruction]
  (let [last-state  (last states-accumulated)
        next-states (calc-states-iteratively
                      last-state
                      instruction)]
    (concat states-accumulated next-states)))

(defn find-not-distinct
  [positions]
  (loop [visited      #{}
        [head & tail] positions]
    (if (or (nil? head) (get visited head))
      head
      (recur (conj visited head) tail))))

(defn calc-displacement
  [{x1 :x y1 :y}
   {x2 :x y2 :y}]
  (let [x (Math/abs (- x2 x1))
        y (Math/abs (- y2 y1))]
    (+ x y)))

(defn part1
  [instructions]
  (->>
    instructions
    (reduce calc-next-state initial-state)
    :position
    (calc-displacement
       (:position initial-state))))

(defn part2
  [instructions]
  (->>
    instructions
    (reduce calc-all-intermediate-states [initial-state])
    (map :position)
    find-not-distinct
    (calc-displacement (:position initial-state))))

(defn -main
  "Advent of Code '16 - Day 1
  How far away is the HQ?"
  [& args]
  (let [instructions
        (->>
          (read-inputs filename)
          inputs->instructions)]
    (do
      (println "The answers for day 1 are:")
      (println "part 1:" (part1 instructions))
      (println "part 2:" (part2 instructions)))))

