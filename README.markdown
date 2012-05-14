# Context.IO API wrapper for Clojure

Access the Context.IO API from Clojure.

## Building

    $ lein deps
    $ lein jar

## Usage

``` clojure
(require 'context-io)

;; Make an OAuth consumer
(def oauth-consumer (context-io/make-consumer <key> <secret>))

;; List your accounts
(context-io/list-accounts oauth-consumer)
```

## License

Copyright (C) 2012 Henrik Hodne. See LICENSE for details.
