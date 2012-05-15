# Context.IO API wrapper for Clojure

Access the Context.IO API from Clojure.

## Usage

``` clojure
(ns mynamespace
  (:use
    [context-io.oauth]
    [context-io.api.two]))

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

## Versioning

This project follows [Semantic Versioning][semver]. Note that the current
version is less than 1.0, which means that the public API is not yet defined
and could change at any time. However, I am fairly certain that this will be
the public API, and the only thing I might change are the names of some of the
functions in `context-io.api.two`. So experiment away, but I'd wait until at
least 0.1 or 0.2 before using this in production.

[semver]: http://semver.org

## License

Copyright (C) 2012 Henrik Hodne. See LICENSE for details.
