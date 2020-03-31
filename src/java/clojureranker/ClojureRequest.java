package clojureranker;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import org.apache.solr.common.util.IOUtils;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.DocListAndSet;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class ClojureRequest extends RequestHandlerBase {

    IFn handle;

    @Override
    public void init(NamedList args) {
        super.init(args);
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("clojureranker.request"));
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        System.out.println("--- Initializing");
    }


    @Override
    public void handleRequestBody(SolrQueryRequest solrQueryRequest, SolrQueryResponse solrQueryResponse) throws Exception {
        System.out.println("--- content-streams: " + solrQueryRequest.getContentStreams());
        System.out.println("Got a request handler..");
        handle = Clojure.var("clojureranker.request", "request");
        handle.invoke(solrQueryRequest, solrQueryResponse);

    }

    @Override
    public String getDescription() {
        return null;
    }

}
