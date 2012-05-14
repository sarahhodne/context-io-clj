(ns context-io.api.two
  (:use
    [context-io core callbacks api]))

(def ^:dynamic *context-io-api* (make-api-context "https" "api.context.io" "2.0"))

(defmacro def-context-io-normal-method
  "Defines a synchronous, single method using the supplied API context"
  [name action resource-path & rest]
  `(def-context-io-method ~name
      ~action
      ~resource-path
      :api ~*context-io-api*
      :callbacks (get-default-callbacks)
      ~@rest))

(def-context-io-normal-method list-accounts :get "accounts")
(def-context-io-normal-method get-account :get "accounts/{:id}")