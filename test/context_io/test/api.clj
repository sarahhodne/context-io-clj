(ns context-io.test.api
  (:use
    [clojure.test]
    [context-io.api])
  (:import
    (context_io.api ApiContext)))

(deftest test-make-uri
  (is (= (make-uri
          (ApiContext. "https" "api.context.io" "2.0")
          "accounts")
         "https://api.context.io/2.0/accounts")))

(deftest test-subs-uri
  (is (= (subs-uri
          "accounts/{:id}"
          {:id 123})
         "accounts/123")))