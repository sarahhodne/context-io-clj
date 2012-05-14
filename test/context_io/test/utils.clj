(ns context-io.test.utils
  (:use
    [clojure.test]
    [context-io.utils]))

(deftest test-transform-map
  (is (= (transform-map {:a 0 :b 1 :c 2 :d 3} :key-trans name)
         {"a" 0 "b" 1 "c" 2 "d" 3}))
  (is (= (transform-map {:a 0 :b 1 :c 2 :d 3} :val-trans inc)
         {:a 1 :b 2 :c 3 :d 4})))