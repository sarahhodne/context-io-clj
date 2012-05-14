(ns context-io.test.core
  (:use
    [clojure.test]
    [context-io.core]))

(deftest test-fix-keyword
  (is (= (#'context-io.core/fix-keyword :my-test) :my_test)))

(deftest test-fix-colls
  (is (= (#'context-io.core/fix-colls :a) :a))
  (is (= (#'context-io.core/fix-colls [1 2 3]) "1,2,3")))

(deftest test-add-form-content-type
  (is (=
        (#'context-io.core/add-form-content-type {})
        {:content-type "application/x-www-form-urlencoded"})
      (=
        (#'context-io.core/add-form-content-type {:foo 0})
        {:foo 0 :content-type "application/x-www-form-urlencoded"})))