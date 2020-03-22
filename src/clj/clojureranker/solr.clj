(ns clojureranker.solr
  (:require [[org.clojure/tools.nrepl "0.2.13"] as nrepl])
  )


(def started (atom false))

(defn start-nrepl []
  (when (not @started)
    (repl/start-server :port 7888)
    )
  )

(defn hupp []
  (println "---- HUPP 2"))
