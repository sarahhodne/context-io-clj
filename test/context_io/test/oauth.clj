(ns context-io.test.oauth
  (:use
    [clojure.test]
    [context-io.oauth]))

(defn make-bogus-creds
  "Makes an OAuth structure with faux credentials."
  []

  (make-oauth-creds "consumer-key" "consumer-secret"))

(deftest test-sign-query
  (let [result (sign-query (make-bogus-creds) :get "http://www.cnn.com" :query {:test-param "true"})]
    (is (:oauth_signature result))))

(deftest test-oauth-header-string
  (is (= (oauth-header-string {:a 1 :b 2 :c 3})
         "OAuth a=\"1\",c=\"3\",b=\"2\""))
  (is (= (oauth-header-string {:a "hi there"})
         "OAuth a=\"hi%20there\""))
  (is (= (oauth-header-string {:a "hi there"} :url-encode? nil)
         "OAuth a=\"hi there\"")))