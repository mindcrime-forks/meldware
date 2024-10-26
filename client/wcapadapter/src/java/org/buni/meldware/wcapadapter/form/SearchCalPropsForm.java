package org.buni.meldware.wcapadapter.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class SearchCalPropsForm extends CalForm {
    private String maxResults;
    private String name;
    private String primaryOwner;
    private String searchOpts;
    private String searchString;
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	    this.maxResults = null;
        this.name=null;
        this.primaryOwner=null;
        this.searchOpts = null;
        this.searchString=null;
		super.reset(mapping, request);
	}
    public String getMaxResults() {
        return maxResults;
    }
    public void setMaxResults(String maxResults) {
        this.maxResults = maxResults;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPrimaryOwner() {
        return primaryOwner;
    }
    public void setPrimaryOwner(String primaryOwner) {
        this.primaryOwner = primaryOwner;
    }
    public String getSearchOpts() {
        return searchOpts;
    }
    public void setSearchOpts(String searchOpts) {
        this.searchOpts = searchOpts;
    }
    public String getSearchString() {
        return searchString;
    }
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
