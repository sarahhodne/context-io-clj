(ns context-io
  (:use [clojure.data.json :only [read-json]])
  (:require [clojure.set :as set]
            [clojure.string :as string]
            [com.twinql.clojure.http :as http]
            [oauth.client :as oauth]
            [oauth.signature])
  (:import (java.io File)
           (org.apache.http.entity.mime.content FileBody)
           (org.apache.http.entity.mime MultipartEntity)))

(declare status-handler)

(def ^:dynamic *oauth-consumer* nil)
(def ^:dynamic *protocol* "https")

(defn make-consumer
  "Make an OAuth consumer for two-legged requests"
  [consumer-key consumer-secret]
  (oauth/make-consumer consumer-key consumer-secret "" "" "" :hmac-sha1))

;; Get JSON from clj-apache-http
(defmethod http/entity-as :json [entity as state]
  (read-json (http/entity-as entity :string state)))

(defmacro with-oauth
  "Set the OAuth consumer to be used for all contained Context.IO requests"
  [consumer & body]
  `(binding [*oauth-consumer* ~consumer]
    (do
      ~@body)))

(defmacro def-context-io-method
  "Given basic specifications of a Context.IO API method, build a function that
   will take any required and optional arguments and call the associated
   Context.IO method"
  [method-name req-method req-url required-params optional-params handler]
  (let [required-fn-params (vec (sort (map #(symbol (name %))
                                           required-params)))
        optional-fn-params (vec (sort (map #(symbol (name %))
                                           optional-params)))]
    `(defn ~method-name
        [consumer# ~@required-fn-params & rest#]
        (let [req-uri# (str *protocol* "://" ~req-url)
              rest-map# (apply hash-map rest#)
              provided-optional-params# (set/intersection (set ~optional-params)
                                                          (set (keys rest-map#)))
              required-query-param-names#
                (map (fn [x#]
                        (keyword (string/replace (name x#) #"-" "_")))
                  ~required-params)

              optional-query-param-names-mapping#
                (map (fn [x#]
                        [x# (keyword (string/replace (name x#) #"-" "_"))])
                  provided-optional-params#)

              query-params# (merge (apply hash-map
                                          (vec (interleave
                                                required-query-param-names#
                                                ~required-fn-params)))
                                   (apply merge
                                          (map (fn [x#] {(second x#)
                                                         ((first x#)
                                                          rest-map#)})
                                               optional-query-param-names-mapping#)))

              need-to-url-encode# (if (= :get ~req-method)
                                    (into {}
                                          (map (fn [[k# v#]]
                                                  [k#
                                                    (oauth.signature/url-encode v#)])
                                               query-params#))
                                    query-params#)
              oauth-creds# (when consumer#
                              (oauth/credentials consumer# nil nil
                                                 ~req-method
                                                 req-uri#
                                                 need-to-url-encode#))]
          (~handler (~(symbol "http" (name req-method))
                    req-uri#
                    :query (merge query-params#
                                  oauth-creds#)
                    :parameters (http/map->params
                                 {:use-expect-continue false})
                    :as :json))))))

;; Accounts

(def-context-io-method list-accounts
  :get
  "api.context.io/2.0/accounts"
  []
  [:email
   :status
   :status-ok
   :limit
   :offset]
  (comp #(:content %) status-handler))

;; Connect tokens

(def-context-io-method list-connect-tokens
  :get
  "api.context.io/2.0/connect_tokens"
  []
  []
  (comp #(:content %) status-handler))

(defn status-handler
  "Handle the various HTTP status codes that may be returned when accessing the
   Context.IO API."
  [result]
  (condp #(if (coll? %1)
              (first (filter (fn [x] (== x %2)) %1))
              (== %2 %1)) (:code result)
    200 result
    304 nil
    [400 401 403 404 406 500 502 503]
    (let [body (:content result)
          headers (into {} (:headers result))
          error-msg (:error body)
          error-code (:code result)
          request-uri (:request body)]
      (throw (proxy [Exception]
                    [(str "[" error-code "] " error-msg ". [" request-uri "]")]
                (request [] (body "request")))))))