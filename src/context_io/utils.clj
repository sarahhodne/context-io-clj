(ns context-io.utils "Helper functions")

(defn assert-throw
  "Throws an exception if the supplied argument is nil.

   val - The value to check.
   msg - The message to give to the exception.

   Throws an exception with the given message if val is nil.
   Returns val otherwise."
  [val msg]
  (or val (throw (Exception. msg))))

(defn transform-map
  "Transform the key and value pairs of a map using separate functions

   map-to-transform - The map to transform.
   :key-trans       - The function to use for transforming the keys (optional)
   :val-trans       - The function to use for transforming the values (optional)

   Returns the map with the keys and values transformed by the functions given."
  [map-to-transform & {:keys [key-trans val-trans]
                       :or {key-trans identity val-trans identity}}]
  (if map-to-transform
    (into {} (map (fn [[k v]] [(key-trans k) (val-trans v)]) map-to-transform))))