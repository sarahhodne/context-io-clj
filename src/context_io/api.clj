(ns context-io.api
  (:use
    [context-io utils]))

(defrecord ApiContext
  [^String protocol
   ^String host
   ^String version])

(defn make-api-context
  [protocol host version]
  (ApiContext. protocol host version))

(defn make-uri
  "Makes a URI from a supplied protocol, site, version and resource-path"
  [^ApiContext context
   ^String resource-path]
  (let [protocol (:protocol context)
        host (:host context)
        version (:version context)]
    (str protocol "://" host "/" version "/" resource-path)))

(defn subs-uri
  "Substitutes parameters for tokens in the URI"
  [uri params]
  (loop [matches (re-seq #"\{\:(\w+)\}" uri)
         ^String result uri]
    (if (empty? matches) result
        (let [[token kw] (first matches)
              value (get params (keyword kw))]
          (assert-throw value
                        (format "%s needs :%s param to be supplied" uri kw))
          (recur (rest matches) (.replace result token (str value)))))))