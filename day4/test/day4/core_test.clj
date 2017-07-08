(ns day4.core-test
  (:require [clojure.test :refer :all]
            [day4.core :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(def room-substring-generator
  (gen/fmap
    (comp clojure.string/lower-case (partial apply str))
    (gen/not-empty
      (gen/vector gen/char-alpha))))

(def room-cipher-alpha-generator
  (gen/fmap
    (partial clojure.string/join "-")
    (gen/not-empty
      (gen/vector room-substring-generator))))

(def room-checksum-generator
  (gen/fmap
    (comp clojure.string/lower-case (partial apply str))
    (gen/vector gen/char-alpha 5)))

(defn concat-room-strings
  [names sector checksum]
  (str names "-" sector "[" checksum "]"))

;; completely random rooms that can be parsed
;; but are almost for sure not "real rooms"
(def random-room-code-generator
  (gen/fmap
    (partial apply concat-room-strings)
    (gen/tuple
      room-cipher-alpha-generator
      gen/s-pos-int
      room-checksum-generator)))

(testing "room generation and destructuring"
  (defspec destructuring-rooms-should-give-back-parts
    (prop/for-all [[cipher sector checksum]
                    (gen/tuple
                      room-cipher-alpha-generator
                      gen/s-pos-int
                      room-checksum-generator)]
      (let [room (concat-room-strings
                   cipher
                   sector
                   checksum)
            destructured-room (destruct-line room)]
      (= (:cipher-text destructured-room)
         cipher)
      (= (:sector destructured-room)
         sector)
      (= (:checksum destructured-room)
         checksum)))))

