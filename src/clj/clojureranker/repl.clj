(ns clojureranker.repl
  (:require [nrepl.server :refer [start-server stop-server]])
  )

(def started (atom false))
(def repl-lock (Object.))

(defn startnrepl
  "start a repl at port 7888 and make sure that only one repl is started"
  []
  (locking repl-lock (when (not @started)
                       (reset! started true)
                       (start-server :port 7888)
                       (reset! started true)
                       (println "Repl started at port 78888. Enjoy!")
                       ))
  )