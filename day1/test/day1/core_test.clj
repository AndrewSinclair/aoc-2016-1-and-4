(ns day1.core-test
  (:require [clojure.test :refer :all]
            [day1.core :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(def directions [\R \L])
(def headings [:north :east :south :west])

(def instruction-generator
  (gen/tuple
    (gen/elements directions)
    gen/s-pos-int))

(def instruction-string-generator
  (gen/fmap
    (partial apply str)
    instruction-generator))

(def position-generator
  (gen/fmap
    (partial apply ->Position)
    (gen/tuple gen/int gen/int)))

(testing "parsing of input"
  (defspec parsing-the-instruction-string-will-give-direction-and-distance
    100
    (prop/for-all [instruction-str instruction-string-generator]
      (let [instruction (str->instruction instruction-str)
            direction   (:direction instruction)
            distance    (:distance  instruction)]
        (= instruction-str (str direction distance))))))

(testing "changing of state (heading and position)"
  (defspec turning-same-way-4-times-will-face-you-in-same-dirction
    100
    (prop/for-all [direction (gen/elements directions)
                   heading   (gen/elements headings)]
      (= heading
         (->>
           direction
           (replicate 4)
           (reduce calc-next-heading heading)))))

  (defspec turning-once-should-always-give-different-direction
    100
    (prop/for-all [direction (gen/elements directions)
                   heading   (gen/elements headings)]
      (not= heading
            (calc-next-heading heading direction))))

  (defspec displacing-some-distance-should-always-give-different-state
    100
    (prop/for-all [distance gen/s-pos-int
                   heading  (gen/elements headings)
                   position position-generator]
      (not= position
            (calc-next-position heading position distance))))

  (defspec moving-some-distance-should-calc-to-moving-that-far
    100
    (prop/for-all [instruction instruction-generator
                   position    position-generator
                   heading     (gen/elements headings)]
      (let [distance      (second instruction)
            next-position (calc-next-position
                            heading
                            position
                            distance)]
        (= distance
           (calc-displacement position next-position)
      )))))

(testing "iteratively incrementing position between two states"
  (defspec number-of-positions-returned-should-be-equal-to-distance
    100
    (prop/for-all [instruction instruction-generator
                   position    position-generator
                   heading     (gen/elements headings)]
      (let [distance (second instruction)
            next-positions (calc-positions-iteratively
                             heading
                             position
                             distance)]
        (= distance
           (count next-positions)))))

  (defspec all-position-will-have-same-x-or-y-depending-on-heading
    100
    (prop/for-all [instruction instruction-generator
                   position    position-generator
                   heading     (gen/elements headings)]
      (let [distance (second instruction)
            next-positions (calc-positions-iteratively
                             heading
                             position
                             distance)
            {x :x y :y} position]
        (if
          (or (= heading :north)
              (= heading :south))
            (every? #(= x (:x %)) next-positions)
            (every? #(= y (:y %)) next-positions))))))

(testing "function that finds the first duplicate"
  (defspec no-duplicates-will-return-nil
    100
    (prop/for-all [nums (gen/vector-distinct gen/int)]
      (nil? (find-not-distinct nums))))
  
  (defspec if-there-are-duplicates-it-will-return-it
    100
    (prop/for-all
      [nums (gen/let
              [distinct-nums (gen/vector-distinct gen/int)
               duplicate (gen/such-that
                          #(not (.contains distinct-nums %))
                          gen/int)]
              {:vec (conj distinct-nums duplicate duplicate)
               :duplicate duplicate})]
      (let [{xs :vec duplicate :duplicate} nums]
        (->>
          xs
          shuffle
          find-not-distinct
          (= duplicate))))))


(deftest day1-answers
  (let [data 
        (->>
          (read-inputs filename) ;; this performs io
          inputs->instructions)]
    (is (= (part1 data) 234))
    (is (= (part2 data) 113))))

