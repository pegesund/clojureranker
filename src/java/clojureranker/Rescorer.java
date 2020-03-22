package clojureranker;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;


import java.io.IOException;

public class Rescorer extends SearchComponent {


    @Override
    public void init(NamedList args) {
        super.init(args);
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("clojureranker.solr"));
        // IFn startServer = Clojure.var("clojureranker.sol", "start-nrepl");
        // startServer.invoke();
    }

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        System.out.println("In prepare");
        IFn require = Clojure.var("clojureranker.solr", "hupp");
        require.invoke();
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        System.out.println("In Process");

    }

    @Override
    public String getDescription() {
        return "-- Petters description";
    }
}
