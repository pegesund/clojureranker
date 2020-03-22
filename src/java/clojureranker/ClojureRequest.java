package clojureranker;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class ClojureRequest extends RequestHandlerBase {

    @Override
    public void init(NamedList args) {
        super.init(args);
        System.out.println("--- Initializing");
    }


    @Override
    public void handleRequestBody(SolrQueryRequest solrQueryRequest, SolrQueryResponse solrQueryResponse) throws Exception {

    }

    @Override
    public String getDescription() {
        return null;
    }
}
