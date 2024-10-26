package org.buni.meldware.mail.mailbox.search;

import java.util.List;
import java.util.Set;

public class AndSearchQuery extends SearchQuery {

    private final List<SearchQuery> queries;

    public AndSearchQuery(List<SearchQuery> queries) {
        this.queries = queries;
    }

    @Override
    public Set<Long> getResults(SearchContext context) {
        Set<Long> results = null;
        for (SearchQuery query : queries) {
            if (results == null) {
                results = query.getResults(context);
            } else {
                results.retainAll(query.getResults(context));
            }
            if (results.size() == 0) {
                break;
            }
        }
        
        return results;
    }

}
