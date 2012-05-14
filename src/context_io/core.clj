(ns context-io.core
  (:use
    [context-io utils api oauth request])
  (:require
    [clojure.string :as string]
    [http.async.client :as ac])
  (:import
    (clojure.lang Keyword PersistentArrayMap)))

(defn- fix-keyword
  "Takes a parameter name and replaces the -s with _s"
  [param-name]
  (keyword (.replace (name param-name) \- \_)))

(defn- fix-colls
  "Turns collections into their string, comma-separated equivalents."
  [val]
  (if (coll? val) (string/join "," val) val))

(defn- add-form-content-type
  "Adds a content type of url-encoded-form to the supplied headers."
  [headers]
  (merge headers
    {:content-type "application/x-www-form-urlencoded"}))

(def memo-create-client (memoize ac/create-client))

(defn default-client
  "Makes a default async client for the HTTP communications."
  []
  (memo-create-client :follow-redirects false))

(defn- get-request-args
  "Takes URI, action and optional args and returns the final URI and HTTP
   parameters for the subsequent call. Note that the parameters are transformed
   (from lispy -s to x-header-style _s) and added to the query. So :params could
   be {:screen-name 'blah'}, and it would be merged into :query as {:screen_name
   'blah'}. The URI has the parameters substituted in, so {:id} in the URI would
   use the :id in the :params map. Also, the OAuth headers are added."
  [^Keyword action
   ^String uri
   ^PersistentArrayMap arg-map]
  (let [params (transform-map (:params arg-map)
                              :key-trans fix-keyword
                              :val-trans fix-colls)
        body (:body arg-map)
        query (merge (:query arg-map) params)
        final-uri (subs-uri uri params)
        oauth-map (sign-query (:oauth-creds arg-map)
                              action
                              final-uri
                              :query query)
        headers (merge (:headers arg-map)
                       {:Authorization (oauth-header-string oauth-map)})
        my-args (cond (= action :get) (hash-map :query query
                                                :headers headers
                                                :body body)
                      (nil? body) (hash-map
                                    :headers (add-form-content-type headers)
                                    :body query)
                      :else (hash-map :query query
                                      :headers headers
                                      :body body))]
    {:action action
     :uri final-uri
     :processed-args (merge (dissoc arg-map :query :headers :body :params
                                            :oauth-creds :client :api
                                            :callbacks)
                            my-args)}))

(defn http-request
  "Calls the action on the resource specified in the URI, signing with OAuth in
   the headers. You can supply args for async.http.client (e.g. :query, :body
   :headers, etc.)"
  [^Keyword action
   ^String uri
   ^PersistentArrayMap arg-map]
  (let [client (or (:client arg-map) (default-client))
        callbacks (or (:callbacks arg-map)
                      (throw (Exception. "need to specify a callback argument for http-request")))
        request-args (get-request-args action uri arg-map)
        request (apply prepare-request-with-multi
                       (:action request-args)
                       (:uri request-args)
                       (apply concat (:processed-args request-args)))]
    (execute-request-callbacks client request callbacks)))

(defmacro def-context-io-method
  "Declares a Context.IO method with the supplied name, HTTP verb and relative
   resource path. As part of the specification, it must have an :api and
   :callbacks member of the 'rest' list. From these it creates a URI, the API
   context and relative resource path. The default callbacks that are supplied
   determine how to make the call (in terms of sync/async)"
  [name action resource-path & rest]
  (let [rest-map (apply sorted-map rest)]
    `(defn ~name
      [creds# & {:as args#}]
      (let [arg-map# (merge ~rest-map args# {:oauth-creds creds#})
            api-context# (assert-throw (:api arg-map#) "must include an ':api entry in the params")
            uri# (make-uri api-context# ~resource-path)]
        (http-request ~action uri# arg-map#)))))