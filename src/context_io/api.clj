(ns context-io.api
  "Helper functions for generating URIs"
  (:use
    [context-io utils]))

(defrecord ApiContext
  [^String protocol
   ^String host
   ^String version])

(defn make-api-context
  "Make an API context with the given information

   protocol - The protocol to use. Should probably be \"http\" or \"https\".
   host     - The hostname for the API endpoint.
   version  - The API version to use.

   Examples

     (make-api-context \"https\" \"api.context.io\" \"2.0\")

   Returns an ApiContext that will point to the API endpoints at
     <protocol>://<host>/<version>/"
  [protocol host version]
  (ApiContext. protocol host version))

(defn make-uri
  "Makes a URI given the supplied API context and resource path

   context       - The API context to use for the base URI.
   resource-path - The path to append to the base URI.

   Examples

     (make-uri (make-api-context \"https\" \"api.context.io\" \"2.0\")
               \"accounts\")
     ; -> \"https://api.context.io/2.0/accounts\""
  [^ApiContext context
   ^String resource-path]
  (let [protocol (:protocol context)
        host (:host context)
        version (:version context)]
    (str protocol "://" host "/" version "/" resource-path)))

(defn subs-uri
  "Substitutes parameters for tokens in the URI

   uri    - The URI with zero or more tokens. Tokens are basically keywords
            inside curly braces, like this: \"{:some-token}\"
   params - A map that maps tokens to their string values. The keys should be
            keywords corresponding to the tokens in the URI.

   Examples

     (subs-uri \"https://api.context.io/2.0/accounts/{:account-id}\"
               {:account-id \"abcdef\"})
     ; -> \"https://api.context.io/2.0/accounts/abcdef\""
  [uri params]
  (loop [matches (re-seq #"\{\:(\w+)\}" uri)
         ^String result uri]
    (if (empty? matches) result
        (let [[token kw] (first matches)
              value (get params (keyword kw))]
          (assert-throw value
                        (format "%s needs :%s param to be supplied" uri kw))
          (recur (rest matches) (.replace result token (str value)))))))