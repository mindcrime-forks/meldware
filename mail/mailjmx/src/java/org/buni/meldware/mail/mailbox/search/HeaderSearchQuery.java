package org.buni.meldware.mail.mailbox.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.buni.meldware.mail.mailbox.FolderEntry;
import org.buni.meldware.mail.message.MailHeaders;
import org.buni.meldware.mail.message.MailHeadersImpl;

public class HeaderSearchQuery extends SearchQuery {

    private final String name;
    private final String value;

    public HeaderSearchQuery(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Set<Long> getResults(SearchContext context) {
        EntityManager em = context.getEntityManager();
        Query q = em.createNamedQuery(FolderEntry.BY_FOLDER);
        q.setParameter(1, context.getFolderId());
        @SuppressWarnings("unchecked")
        List<FolderEntry> fes = (List<FolderEntry>) q.getResultList();
        Set<Long> ids = new HashSet<Long>();
        
        for (FolderEntry fe : fes) {
            MailHeaders mh = MailHeadersImpl.create(fe.getMessage().getHeaders());
            for (String val : mh.getHeader(name)) {
                if (val.toUpperCase().indexOf(value.toUpperCase()) != -1) {
                    ids.add(fe.getUid());
                    break;
                }
            }
        }
        
        return ids;
    }

}
