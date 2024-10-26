package org.buni.meldware.mail.mailbox.search;

import javax.persistence.Query;

import org.buni.meldware.mail.mailbox.search.EJBQLSearchQuery.Operator;

/**
 * General String Value, will convert the value to upper case and
 * wrap it in '%'.  It will also convert the field to upper case.
 * @author Michael.Barker
 *
 */
public class StringSearchValue extends SearchValue {

    private String value;
    private Operator op;

    public StringSearchValue(Operator op, String value) {
        this.op = op;
        this.value = value;
    }
    
    @Override
    public void setParameter(int idx, Query q) {
        q.setParameter(idx, "%" + value.toUpperCase() + "%");
    }

    @Override
    public String getField(String fieldName) {
        return "UPPER(" + fieldName + ")";
    }
    
    @Override
    public String getClause(String fieldName, int idx) {
        return String.format("UPPER(%s) %s ?%d", fieldName, getOp(op), idx);
    }
}
