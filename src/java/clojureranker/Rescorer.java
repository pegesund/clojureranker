package clojureranker;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.search.DocList;
import org.apache.solr.search.DocListAndSet;


import java.io.IOException;

public class Rescorer extends SearchComponent {

    IFn prepare;
    IFn process;

    @Override
    public void init(NamedList args) {
        super.init(args);
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("clojureranker.solr"));
        IFn hupp = Clojure.var("clojureranker.solr", "startnrepl");
        hupp.invoke();
        prepare = Clojure.var("clojureranker.solr", "prepare");
        process = Clojure.var("clojureranker.solr", "process");

    }

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        System.out.println("In prepare");
        IFn hupp = Clojure.var("clojureranker.solr", "hupp");
        hupp.invoke();
        prepare.invoke(responseBuilder);
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        System.out.println("In Process");
        process.invoke(responseBuilder);
    }

    @Override
    public String getDescription() {
        return "-- Petters description";
    }
}
