(ns harland.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [harland.core :refer :all]))

(deftest a-test
  (fact "This test will fail"
    (+ 1 1) => 1))
