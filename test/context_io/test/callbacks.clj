(ns context-io.test.callbacks
  (:use
    [clojure.test]
    [context-io.callbacks])
  (:import
    (context_io.callbacks Callback)))

(deftest test-callback
  (let [s (Callback. identity identity identity)]
    (is (= ((:on-success s) "test") "test"))
    (is (= ((:on-failure s) "test") "test"))
    (is (= ((:on-exception s) "test") "test"))))

(deftest test-get-default
  (is (= (type (get-default-callbacks)) context_io.callbacks.Callback)))