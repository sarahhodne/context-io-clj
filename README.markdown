# Context.IO API wrapper for Clojure

Access the Context.IO API from Clojure.

## Usage

``` clojure
(ns mynamespace
  (:use
    [context-io.oauth]
    [context-io.api]))

;; Make an OAuth consumer
(def ^:dynamic *creds* (make-oauth-creds <key> <secret>))

;; List your accounts
(list-accounts *creds*)
```

## Building

Simply use leiningen to build the library into a jar, like this:

    $ git clone git://github.com/dvyjones/context-io-clj.git
    $ cd context-io-clj
    $ lein jar

## License

Copyright (C) 2012 Henrik Hodne. See LICENSE for details.
