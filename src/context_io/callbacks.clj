(ns context-io.callbacks
  "Functions for dealing with callbacks"
  (:require
    [clojure.data.json :as json]
    [http.async.client :as ac]
    [http.async.client.request :as req]))

(defrecord Callback [on-success on-failure on-exception])

(defn response-return-everything
  "Callback that returns the body parsed as JSON and response metadata

   response  - The response from http.async.client.
   :to-json? - Whether to parse the body or not. (optional, default is true)

   Examples

     (response-return-everything response :to-json? false)

   Returns a map with the following keys: :headers, :status and :body."
  [response & {:keys [to-json?] :or {to-json? true}}]
  (let [body-trans (if to-json? json/read-json identity)]
    (hash-map :headers (ac/headers response)
              :status (ac/status response)
              :body (body-trans (ac/string response)))))

(defn response-throw-error
  "Throws an exception with a generic message.

   response - The response from http.async.client

   This will be changed to throwing an exception with more information about
   the error returned."
  [response]
  (throw (Exception. "An error occured"))) ; TODO: Fix message

(defn exception-rethrow
  "Rethrows the exception given.

   response  - The response from http.async.client.
   throwable - The exception to re-raise.

   Throws throwable."
  [response throwable]
  (throw throwable))

(defn get-default-callbacks
  "Get the default callbacks used for most requests

   Returns a Callback object using response-return-everything for the
     :on-success event, response-throw-error for the :on-failure event and
     exception-rethrow for the :on-exception event."
  []
  (Callback. response-return-everything response-throw-error exception-rethrow))

(defn emit-callback-list
  [_]
  req/*default-callbacks*)

(defn handle-response
  "Handle the response, calling the correct callback

   response  - The response from http.async.client.
   callbacks - A map with the callback functions.
   :events   - The events that this may call.

   Examples

     (handle-response response {:on-success response-return-everything}
                      :events #{:on-success})

   Returns the return value of the callback that got run."

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