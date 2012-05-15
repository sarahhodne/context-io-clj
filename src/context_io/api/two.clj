(ns context-io.api.two
  (:use
    [context-io core callbacks api]))

(def ^:dynamic *context-io-api* (make-api-context "https" "api.context.io" "2.0"))

(defmacro def-context-io-two-method
  "Defines a synchronous, single method using the supplied API context"
  [name action resource-path & rest]
  `(def-context-io-method ~name
      ~action
      ~resource-path
      :api ~*context-io-api*
      :callbacks (get-default-callbacks)
      ~@rest))

;; OAuth Providers
(def-context-io-two-method list-oauth-providers :get "oauth_providers")
(def-context-io-two-method create-oauth-provider :post "oauth_providers")
(def-context-io-two-method get-oauth-provider :get "oauth_providers/{:key}")
(def-context-io-two-method remove-oauth-provider :delete "oauth_providers/{:key}")

;; Discovery
(def-context-io-two-method discovery :get "discovery")

;; Connect Tokens
(def-context-io-two-method list-connect-tokens :get "connect_tokens")
(def-context-io-two-method create-connect-token :post "connect_tokens")
(def-context-io-two-method get-connect-token :get "connect_tokens/{:token}")
(def-context-io-two-method remove-connect-token :delete "connect_tokens/{:token}")

;; Accounts
(def-context-io-two-method list-accounts :get "accounts")
(def-context-io-two-method create-account :post "accounts")
(def-context-io-two-method get-account :get "accounts/{:id}")
(def-context-io-two-method remove-account :delete "accounts/{:id}")
(def-context-io-two-method modify-account :post "accounts/{:id}")

;; Accounts -> Connect Tokens
(def-context-io-two-method list-account-connect-tokens :get "accounts/{:account-id}/connect_tokens")
(def-context-io-two-method create-account-connect-token :post "accounts/{:account-id}/connect_tokens")
(def-context-io-two-method get-account-connect-token :get "accounts/{:account-id}/connect_tokens/{:token}")
(def-context-io-two-method remove-account-connect-token :delete "accounts/{:account-id}/connect_tokens/{:token}")

;; Accounts -> Contacts
(def-context-io-two-method list-account-contacts :get "accounts/{:account-id}/contacts")
(def-context-io-two-method create-account-contact :post "accounts/{:account-id}/contacts")
(def-context-io-two-method get-account-contact :get "accounts/{:account-id}/contacts/{:email}")
(def-context-io-two-method remove-account-contact :delete "accounts/{:account-id}/contacts/{:email}")

;; Accounts -> Contacts -> Files
(def-context-io-two-method list-account-contact-files :get "accounts/{:account-id}/contacts/{:contact-email}/files")

;; Accounts -> Contacts -> Messages
(def-context-io-two-method list-account-contact-messages :get "accounts/{:account-id}/contacts/{:contact-email}/messages")

;; Accounts -> Contacts -> Threads
(def-context-io-two-method list-account-contact-threads :get "accounts/{:account-id}/contacts/{:contact-email}/threads")

;; Accounts -> Email Addresses
(def-context-io-two-method list-account-email-addresses :get "accounts/{:account-id}/email_addresses")
(def-context-io-two-method create-account-email-address :post "accounts/{:account-id}/email_addresses")
(def-context-io-two-method get-account-email-address :get "accounts/{:account-id}/email_addresses/{:email}")
(def-context-io-two-method remove-account-email-address :delete "accounts/{:account-id}/email_addresses/{:email}")

;; Accounts -> Files
(def-context-io-two-method list-account-files :get "accounts/{:account-id}/files")
(def-context-io-two-method get-account-file :get "accounts/{:account-id}/files/{:file-id}")

;; Accounts -> Files -> Changes
(def-context-io-two-method list-account-file-comparable :get "accounts/{:account-id}/files/{:file-id}/changes")
(def-context-io-two-method list-account-file-compare :get "accounts/{:account-id}/files/{:file-id}/changes/{:other-file-id}")

;; Accounts -> Files -> Content
(def-context-io-two-method get-account-file-content :get "accounts/{:account-id}/files/{:file-id}/content")

;; Accounts -> Files -> Related
(def-context-io-two-method get-account-file-related :get "accounts/{:account-id}/files/{:file-id}/related")

;; Accounts -> Files -> Revisions
(def-context-io-two-method get-account-file-revisions :get "accounts/{:account-id}/files/{:file-id}/revisions")

;; Accounts -> Messages
(def-context-io-two-method list-account-messages :get "accounts/{:account-id}/messages")
;;; TODO: Add POST accounts/{:account-id}/messages (needs multipart)
;;; SEE: http://context.io/docs/2.0/accounts/messages
(def-context-io-two-method get-account-message :get "accounts/{:account-id}/messages/{:message-id}")
(def-context-io-two-method copy-account-message :post "accounts/{:account-id}/messages/{:message-id}")

;; Accounts -> Messages -> Body
(def-context-io-two-method get-account-message-body :get "accounts/{:account-id}/messages/{:message-id}/body")

;; Accounts -> Messages -> Source
(def-context-io-two-method get-account-message-source :get "accounts/{:account-id}/messages/{:message-id}/source")

;; Accounts -> Messages -> Flags
(def-context-io-two-method get-account-message-flags :get "accounts/{:account-id}/messages/{:message-id}/flags")
(def-context-io-two-method set-account-message-flags :post "accounts/{:account-id}/messages/{:message-id}/flags")

;; Accounts -> Messages -> Folders
(def-context-io-two-method list-account-message-folders :get "accounts/{:account-id}/messages/{:message-id}/folders")
(def-context-io-two-method edit-account-message-folders :post "accounts/{:account-id}/messages/{:message-id}/folders")
(def-context-io-two-method set-account-message-folders :put "accounts/{:account-id}/messages/{:message-id}/folders")

;; Accounts -> Messages -> Headers
(def-context-io-two-method list-account-message-headers :get "accounts/{:account-id}/messages/{:message-id}/headers")

;; Accounts -> Messages -> Thread
(def-context-io-two-method list-account-messages-in-thread :get "accounts/{:account-id}/messages/{:message-id}/thread")

;; Accounts -> Sources
(def-context-io-two-method list-account-sources :get "accounts/{:account-id}/sources")
(def-context-io-two-method create-account-source :post "accounts/{:account-id}/sources")
(def-context-io-two-method get-account-source :get "accounts/{:account-id}/sources/{:label}")
(def-context-io-two-method edit-account-source :post "accounts/{:account-id}/sources/{:label}")
(def-context-io-two-method remove-account-source :delete "accounts/{:account-id}/sources/{:label}")

;; Accounts -> Sources -> Folders
(def-context-io-two-method list-account-source-folders :get "accounts/{:account-id}/sources/{:label}/folders")
(def-context-io-two-method create-account-source-folder :put "accounts/{:account-id}/sources/{:label}/folders/{:folder}")

;; Accounts -> Sources -> Folders -> Messages
(def-context-io-two-method list-account-source-folder-messages :get "accounts/{:account-id}/sources/{:label}/folders/{:folder}/messages")

;; Accounts -> Sources -> Sync
(def-context-io-two-method account-source-sync-status :get "accounts/{:account-id}/sources/{:label}/sync")
(def-context-io-two-method sync-account-source :post "accounts/{:account-id}/sources/{:label}/sync")

;; Accounts -> Sync
(def-context-io-two-method account-sync-status :get "accounts/{:account-id}/sync")
(def-context-io-two-method sync-account :post "accounts/{:account-id}/sync")

;; Accounts -> Threads
(def-context-io-two-method list-account-threads :get "accounts/{:account-id}/threads")
(def-context-io-two-method get-account-thread :get "accounts/{:account-id}/threads/{:thread-id}")

;; Accounts -> Webhooks
(def-context-io-two-method list-account-webhooks :get "accounts/{:account-id}/webhooks")
(def-context-io-two-method create-account-webhook :post "accounts/{:account-id}/webhooks")
(def-context-io-two-method get-account-webhook :get "accounts/{:account-id}/webhooks/{:webhook-id}")
(def-context-io-two-method edit-account-webhook :post "accounts/{:account-id}/webhooks/{:webhook-id}")
(def-context-io-two-method remove-account-webhook :delete "accounts/{:account-id}/webhooks/{:webhook-id}")