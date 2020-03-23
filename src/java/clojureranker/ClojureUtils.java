package clojureranker;

import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.search.DocIterator;

import java.util.ArrayList;
import java.util.List;

public class ClojureUtils {

    public static List<Float> iterableToList(DocIterator di, int reRankNum) {
        List<Float> scores = new ArrayList<>();
        int counter = 0;
        for (DocIterator docIterator=di; docIterator.hasNext() &&
                counter < reRankNum; counter++) {
            docIterator.next();
            scores.add(docIterator.score());
        }
        return scores;
    }
}
