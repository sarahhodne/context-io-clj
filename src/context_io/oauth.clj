(ns context-io.oauth
  "Functions for handling the OAuth part of HTTP requests."
  (:require
    [oauth.client :as oa]
    [oauth.signature :as oas]))

; Currently this is only a simple wrapper around the OAuth consumer object
(defrecord OAuthCredentials
  [consumer])

(defn sign-query
  "Returns the signing parameters given information about a request

   oauth-creds - The OAuthCredentials to sign the request with.
   action      - The HTTP action for the request (:get, :post, etc.).
   uri         - The URI for the request.
   :query      - The query parameters for the request (optional).

   Returns a map of parameters to put in the OAuth header string."
  [#^OAuthCredentials oauth-creds action uri & {:keys [query]}]
  (if oauth-creds
    (into (sorted-map)
          (merge {:realm "Context.IO API"}
                 (oa/credentials (:consumer oauth-creds) nil nil
                                 action
                                 uri
                                 query)))))

(defn oauth-header-string
  "Creates the string for the 'Authorization' header.

   signing-map  - The information to put in the header, as returned by
                  sign-query.
   :url-encode? - Whether to URL-encode the values or not (optional, default true).

   Examples

     (oauth-header-string
       (sign-query (make-oauth-creds \"consumer-key\" \"consumer-value\")
                   :get \"https://api.context.io/2.0/accounts\")
       :url-encode false)
     ; -> \"OAuth foo=bar,baz=foobar\"

   Returns a string appropriate for putting in the Authorization header of a
     HTTP request."
  [signing-map & {:keys [url-encode?] :or {url-encode? true}}]
  (let [val-transform (if url-encode? oas/url-encode identity)
        s (reduce (fn [s [k v]] (format "%s%s=\"%s\"," s
                                                       (name k)
                                                       (val-transform (str v))))
                  "OAuth "
                  signing-map)]
    ; Remove the trailing comma
    (.substring s 0 (dec (count s)))))

(defn make-oauth-creds
  "Create an OAuthCredentials object with the given consumer key and secret

   app-key    - The consumer key to use.
   app-secret - The consumer secret to use.

   Returns an OAuthCredentials object, suitable for passing to sign-query."
  [app-key app-secret]
  (let [consumer (oa/make-consumer app-key app-secret nil nil nil :hmac-sha1)]
    (OAuthCredentials. consumer)))