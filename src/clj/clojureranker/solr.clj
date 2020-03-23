(ns clojureranker.solr
  (:require [clojure.tools.nrepl.server :as repl])
  (:import (org.apache.solr.search SolrIndexSearcher)
           (com.google.common.collect Streams ImmutableList)
           (clojureranker ClojureUtils)))


(def started (atom false))

(defn startnrepl []
  (when (not @started)
    (repl/start-server :port 7888)
    (reset! started true)
    (println "Repl started at port 78888")
    )
  )

(defn hupp []
  (println "---- HUPP 34"))


(defn prepare [rb]
  (let [params (.getParams (.req rb))
        sortSpec (.getSortSpec rb)
        offset (.getOffset sortSpec)
        reRankNum 10]
    (when (> reRankNum offset)
      (.setCount sortSpec reRankNum)
      (.setOffset sortSpec 0)
      (when (nil? (.get params "score"))
        (.setFieldFlags rb SolrIndexSearcher/GET_SCORES)))))

(defn process [rb]
  (println "Clojure process")
  (let [
        params (.getParams (.req rb))
        user-id (.get params "userid")
        reRankNum (.size (.docList (.getResults rb)))
        offset (or (Integer. (.get params "start")) 0)
        ]
    (when (>= reRankNum offset)
      (let [initialSearchResult (.docList (.getResults rb))
            score (seq (ClojureUtils/iterableToList (.iterator initialSearchResult) reRankNum))
            docs (iterator-seq (.iterator initialSearchResult))
            ]
        (println score docs)
        (println "initalresults" (.docSet (.getResults rb)))

        )
      )
    )
  )