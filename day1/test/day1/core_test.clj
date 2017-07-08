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
           (reduce calc-next-heading heading))))))

