(ns clojureranker.test)

(defn rescore [score_list]
  "this is only a test rescore function"
  (map (fn [doc]
         (let [old-score (first doc)
               lucene-id (second doc)
               solr-doc (nth doc 2)
               new-score (if (= (.get solr-doc "id") "055357342X") 1 (rand))
               ]
           [new-score lucene-id])
         ) score_list)
  )