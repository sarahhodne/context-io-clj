(ns context-io.oauth
  (:require
    [oauth.client :as oa]
    [oauth.signature :as oas]))

; Currently this is only a simple wrapper around the OAuth consumer object
(defrecord OAuthCredentials
  [consumer])

(defn sign-query
  "Takes OAuth credentials and returns a map of the signing parameters"
  [#^OAuthCredentials oauth-creds action uri & {:keys [query]}]
  (if oauth-creds
    (into (sorted-map)
          (merge {:realm "Context.IO API"}
                 (oa/credentials (:consumer oauth-creds) nil nil
                                 action
                                 uri
                                 query)))))

(defn oauth-header-string
  "Creates the string for the OAuth header's 'Authorization' value, URL-encoding
   each value."
  [signing-map & {:keys [url-encode?] :or {url-encode? true}}]
  (let [val-transform (if url-encode? oas/url-encode identity)
        s (reduce (fn [s [k v]] (format "%s%s=\"%s\"," s
                                                       (name k)
                                                       (val-transform (str v))))
                  "OAuth "
                  signing-map)]
    (.substring s 0 (dec (count s)))))

(defn make-oauth-creds
  "Creates an OAuth object out of supplied params"
  [app-key app-secret]
  (let [consumer (oa/make-consumer app-key app-secret nil nil nil :hmac-sha1)]
    (OAuthCredentials. consumer)))