(defproject clojureranker "0.1.0"
  :description "Rescore Solr scores with clojure"
  :url "https://github.com/pegesund/clojureranker"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.apache.solr/solr-core "8.4.1"]
                 [nrepl "0.7.0"]
                 ]
  :repl-options {:init-ns clojureranker.core}
  :prep-tasks ["javac"]
  :main clojureranker.solr
  :source-paths ["src/clj"]
  :jar-exclusions [#"clojure-1.10.1.*"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  )
