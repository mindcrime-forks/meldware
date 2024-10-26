package org.buni.meldware.mail.mailbox.search;

import javax.persistence.Query;

import org.buni.meldware.mail.mailbox.search.EJBQLSearchQuery.Operator;

public class BooleanSearchValue extends SearchValue {   

    private Boolean b;
    private Operator op;

    public BooleanSearchValue(Operator op, Boolean b) {
        this.op = op;
        this.b = b;
    }

    @Override
    public void setParameter(int idx, Query q) {
        q.setParameter(idx, b);
    }

    @Override
    public String getClause(String fieldName, int idx) {
        return String.format("%s %s ?%d", fieldName, getOp(op), idx);
    }

}
