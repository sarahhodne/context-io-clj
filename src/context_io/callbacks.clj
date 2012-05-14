(ns context-io.callbacks
  (:require
    [clojure.data.json :as json]
    [http.async.client :as ac]
    [http.async.client.request :as req]))

(defrecord Callback [on-success on-failure on-exception])

(defn response-return-everything
  "This takes a response and returns a map of the headers and the JSON-parsed
   body."
  [response & {:keys [to-json?] :or {to-json? true}}]
  (let [body-trans (if to-json? json/read-json identity)]
    (hash-map :headers (ac/headers response)
              :status (ac/status response)
              :body (body-trans (ac/string response)))))

(defn response-throw-error
  "Throws the supplied error in an exception."
  [response]
  (throw (Exception. "An error occured"))) ; TODO: Fix message

(defn exception-rethrow
  "Prints the string version of the throwable object."
  [response throwable]
  (throw throwable))

(defn get-default-callbacks
  "Get the default callbacks"
  []
  (Callback. response-return-everything response-throw-error exception-rethrow))

(defn emit-callback-list
  [_]
  req/*default-callbacks*)

(defn handle-response
  [response callbacks & {:keys [events] :or {events #{:on-success :on-failure}}}]
  (cond
    (and (:on-exception events)
         (ac/error response))
      ((:on-exception callbacks) response (ac/error response))
    (and (:on-success events)
         (< (:code (ac/status response)) 400))
      ((:on-success callbacks) response)
    (and (:on-failure events)
         (>= (:code (ac/status response)) 400))
      ((:on-failure callbacks) response)))