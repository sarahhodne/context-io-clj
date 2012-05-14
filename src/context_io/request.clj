(ns context-io.request
  (:use
    [context-io.callbacks])
  (:require
    [http.async.client :as ac]
    [http.async.client.util :as requ]
    [http.async.client.request :as req]
    [clojure.string :as string])
  (:import
    (com.ning.http.client Cookie PerRequestConfig RequestBuilder)
    (java.io File InputStream)))

(defn execute-request-callbacks
  "Submits the request and then calls back to the callbacks"
  [client req callbacks]
  (let [transform #(handle-response (ac/await %) callbacks :events #{:on-success :on-failure :on-exception})
        response (apply req/execute-request client req (apply concat (emit-callback-list callbacks)))]
    (transform response)))

(defn- add-to-req
  [rb kvs f]
  (doseq [[k v] kvs] (f rb
                        (if (keyword? k) (name k) k)
                        (if (coll? v)
                          (string/join "," v)
                          (str v)))))

(defn- add-headers
  "Adds headers to the request builder."
  [rb headers]
  (add-to-req rb headers #(.addHeader %1 %2 %3)))

(defn- add-cookies
  "Adds the cookies to the request builder."
  [rb cookies]
  (doseq [{:keys [domain name value path max-age secure]
           :or {path "/" max-age 30 secure false}} cookies]
    (.addCookie rb (Cookie. domain name value path max-age secure))))

(defn- add-query-parameters
  "Adds the query parameters to the request builder."
  [rb query]
  (add-to-req rb query #(.addQueryParameter %1 %2 %3)))

(defn- add-body
  "Adds the body (or sequence of bodies) onto the request builder, dealing with
   the special cases."
  [rb body content-type]
  (cond
    (= "multipart/form-data" content-type) (doseq [bp (if (coll? body)
                                                   body (list body))]
                                              (.addBodyPart rb bp))
    (map? body) (doseq [[k v] body]
                  (.addParameter rb
                                 (if (keyword? k) (name k) k)
                                 (str v)))
    (string? body) (.setBody rb (.getBytes
                                  (if (= "application/x-www-form-urlencoded"
                                         content-type)
                                    (req/url-encode body)
                                    body)
                                  "UTF-8"))
    (instance? InputStream body) (.setBody rb body)
    (instance? File body) (.setBody rb body)))

(defn- set-timeout
  "Sets the timeout for the request."
  [rb timeout]
  (let [prc (PerRequestConfig.)]
    (.setRequestTimeoutInMs prc timeout)
    (.setPerRequestConfig rb prc)))

(defn prepare-request-with-multi
  "The same as a normal prepare-request, but deals with multi-part form-data as
   a content-type."
  [method #^String url & {:keys [headers query body cookies proxy auth timeout]}]
  (let [rb (RequestBuilder. (req/convert-method method))]
    (when headers (add-headers rb headers))
    (when query (add-query-parameters rb query))
    (when body (add-body rb body (:content-type headers)))
    (when cookies (add-cookies rb cookies))
    (when auth (requ/set-realm auth rb))
    (when proxy (requ/set-proxy proxy rb))
    (when timeout (set-timeout rb timeout))
    (.. rb (setUrl url) (build))))