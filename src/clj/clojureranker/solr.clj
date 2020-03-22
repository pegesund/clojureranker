(ns clojureranker.solr
  (:require [clojure.tools.nrepl.server :as repl])
  )


(def started (atom false))

(defn start-nrepl []
  (when (not @started)
    (repl/start-server :port 7888)
    )
  )

(defn hupp []
  (println "---- HUPP 2"))
