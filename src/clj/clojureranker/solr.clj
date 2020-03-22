(ns clojureranker.solr
  (:require [clojure.tools.nrepl.server :as repl])
  )


(def started (atom false))

(defn startnrepl []
  (when (not @started)
    (repl/start-server :port 7888)
    (reset! started true)
    (println "Repl started at port 78888")
    )
  )

(defn hupp []
  (println "---- HUPP 33"))
