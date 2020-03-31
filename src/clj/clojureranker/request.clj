(ns clojureranker.request
  (:require [clojure.java.io :as io])
  (:import (org.apache.solr.handler RequestHandlerUtils)
           (org.apache.solr.common.params CommonParams)
           (java.net URI)
           (java.io BufferedReader InputStreamReader)))

(defn request-real [req resp]
  (let [call (.getHttpSolrCall req)
        r (.getReq call)
        input-stream (.getInputStream r)
        ]
    (RequestHandlerUtils/setWt req CommonParams/JSON)
    (println (.getContentStreams req))
    )
  )


(defn request
  "This function is a boilerplate for writing request handlers in an clojure environment.
   Put your code inside the try.."
  [req resp]
  (try
    (request-real req resp)
     (catch Exception e
       (clojure.stacktrace/print-stack-trace e)
       ))
)


