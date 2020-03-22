(defproject clojureranker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.apache.solr/solr-core "8.4.1"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 ]
  :repl-options {:init-ns clojureranker.core}
  :prep-tasks ["javac"]
  :main clojureranker.solr
  :source-paths ["src/clj"]
  :jar-exclusions [#"clojure-1.10.1.*"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  )
