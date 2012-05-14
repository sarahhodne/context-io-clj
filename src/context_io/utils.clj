(ns context-io.utils)

(defn assert-throw
  "If the supplied arg is nil, throw with the exception text provided."
  [val msg]
  (or val (throw (Exception. msg))))

(defn transform-map
  "Transforms the key/value pairs of a map using the supplied transformation
   functions."
  [map-to-transform & {:keys [key-trans val-trans]
                       :or {key-trans identity val-trans identity}}]
  (if map-to-transform
    (into {} (map (fn [[k v]] [(key-trans k) (val-trans v)]) map-to-transform))))