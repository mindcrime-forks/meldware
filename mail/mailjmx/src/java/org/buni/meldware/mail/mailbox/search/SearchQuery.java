package org.buni.meldware.mail.mailbox.search;

import java.util.Set;

public abstract class SearchQuery {

    public abstract Set<Long> getResults(SearchContext context);
    
}
