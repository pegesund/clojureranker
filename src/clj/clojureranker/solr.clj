(ns clojureranker.solr
  (:require [clojure.tools.nrepl.server :as repl])
  (:import (org.apache.solr.search SolrIndexSearcher DocListAndSet DocSlice)
           (com.google.common.collect Streams ImmutableList)
           (clojureranker ClojureUtils)
           (org.apache.solr.response BasicResultContext)))


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

(def rerank-num (atom 1000))

(defn prepare [rb]
  (when (rescore? rb)
    (let [params (.getParams (.req rb))
        sortSpec (.getSortSpec rb)
        offset (.getOffset sortSpec)
        ]
    (when (> @rerank-num offset)
      (.setCount sortSpec @rerank-num)
      (.setOffset sortSpec 0)
      (when (nil? (.get params "rescore"))
        (.setFieldFlags rb SolrIndexSearcher/GET_SCORES))))))


(defn rescore [score_list]
  (map (fn [doc]
         (let [old-score (first doc)
               lucene-id (second doc)
               solr-doc (nth doc 2)
               new-score (if (= (.get solr-doc "id") "055357342X")
                           1
                           (rand))
               ]
           [new-score lucene-id])
         ) score_list)
  )

(defn rescore? [rb]
  (= "true" (.get (.getParams (.req rb)) "rescore")))

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
          response (.rsp rb)
          ]
      (-> response (.getValues) (.removeAll "response") )
      (.addResponse response result-context))))

(defn process [rb]

  (when (rescore? rb) (let [
        params (.getParams (.req rb))
        offset (or (Integer. (.get params "start")) 0)
        searcher (.getSearcher (.req rb))
        ]
    (when (>= @rerank-num offset)
      (let [initialSearchResult (.docList (.getResults rb))
            score (seq (ClojureUtils/iterableToList (.iterator initialSearchResult) @rerank-num))
            lucene-docs (iterator-seq (.iterator initialSearchResult))
            solr-docs (map #(.doc searcher % #{"id"}) lucene-docs)
            composed-list (map vector score lucene-docs solr-docs)
            rescored-list (sort #(compare (first %2) (first %1)) (rescore composed-list))
            ]
        (return-new-result rb rescored-list))))))