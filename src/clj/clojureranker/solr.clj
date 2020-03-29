(ns clojureranker.solr
  (:require [nrepl.server :refer [start-server stop-server]]
            [clojureranker.repl])
  (:import (org.apache.solr.search SolrIndexSearcher DocListAndSet DocSlice)
           (clojureranker ClojureUtils)
           (org.apache.solr.response BasicResultContext)))


(def rescore-default (atom false))
(defonce functions (atom {}))
(defonce tops (atom {}))
(defonce ids (atom {}))
(defonce current-init (atom nil))


(defn rescore? [rb]
  (or @rescore-default
      (= "true" (.get (.getParams (.req rb)) "rescore"))))

(defn init
  "Set init values"
  [args]
  (when args (reset! current-init (.get args "defaults")))
  (let [default (if args (.get args "defaults") @current-init)
        do-start-repl (.getBooleanArg default "start-nrepl")
        do-rescore-default (.getBooleanArg default "rescore")
        name (.get default "searchComponentName")
        a-require (.get default "require")
        a-load-file (.get default "load-file")
        a-function (.get default "function")
        top (or (.get default "top") 50)
        id-field (or (.get default "id") "id")
        ]
    (when do-rescore-default (reset! rescore-default true))
    (when a-require (require (symbol a-require)))
    (when a-load-file (load-file a-load-file))
    (when do-start-repl
      (clojureranker.repl/startnrepl)
      )
    (when a-function
      (let [fun (resolve (symbol a-function))]
        (swap! functions assoc name fun)))
    (swap! tops assoc name top)
    (swap! ids assoc name id-field)))

(defn prepare [rb name]
  (when (rescore? rb)
    (let [params (.getParams (.req rb))
          sortSpec (.getSortSpec rb)
          offset (.getOffset sortSpec)
          top (.get @tops name)
        ]
    (when (> top offset)
      (.setCount sortSpec top)
      (.setOffset sortSpec 0)
      (when (= "true" (.get params "rescore"))
        (.setFieldFlags rb SolrIndexSearcher/GET_SCORES))))))

(defn return-new-result
  "Pass scored must be a list of [[lucene-id score] [lucene-id score]... ]"
  [rb scored-results]
    (let [unzipped-score (apply map vector scored-results)
        scores (into-array Float/TYPE (first unzipped-score))
        lucene-docs (into-array Integer/TYPE (second unzipped-score))
        doc-list (.docList (.getResults rb))
        matches (.matches doc-list)
        max-score (.maxScore doc-list)
        new-offset (.offset doc-list)
        len (count lucene-docs)
        dls (DocListAndSet.)
        doc-slice (DocSlice. new-offset len lucene-docs scores matches max-score)
        ]
    (set! (.docList dls) doc-slice)
    (.setResults rb dls)
    (let [result-context (BasicResultContext. rb)
          response (.rsp rb)]
      (-> response (.getValues) (.removeAll "response") )
      (.addResponse response result-context))))

(defn process [rb name]
  "Get results, rescore and serve new results with new scoring"
  (when (rescore? rb) (let [
        params (.getParams (.req rb))
        offset (Integer. (or (.get params "start") 0))
        rows (if-let [r (.get params "rows")] (Long. r))
        searcher (.getSearcher (.req rb))
        top (.get @tops name)
        ]
    (when (>= top offset)
      (let [initialSearchResult (.docList (.getResults rb))
            score (seq (ClojureUtils/iterableToList (.iterator initialSearchResult) top))
            lucene-docs (iterator-seq (.iterator initialSearchResult))
            id-field-set #{(.get @ids name)}
            solr-docs (map #(.doc searcher % id-field-set) lucene-docs)
            composed-list (map vector score lucene-docs solr-docs)
            the-rescore-function (.get @functions name)
            rescored-list (sort #(compare (first %2) (first %1))
                                (apply the-rescore-function [composed-list]))
            result (if rows (take rows rescored-list) rescored-list)
            ]
        (return-new-result rb result))))))