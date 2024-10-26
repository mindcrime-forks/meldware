package org.buni.meldware.wcapadapter.action;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward; 
import org.apache.struts.action.ActionMapping;
import org.buni.meldware.calendar.data.Invite;
import org.buni.meldware.calendar.interfaces.PIMServiceLocal;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.common.util.GMTTimeFormat;
import org.buni.meldware.wcapadapter.form.FetchComponentsByRangeForm;
import org.buni.meldware.wcapadapter.form.SearchCalPropsForm;
import org.buni.meldware.wcapadapter.session.WCAPSession;
import org.buni.meldware.wcapadapter.bean.CalInfo;

public class SearchCalProps extends WCAPSessionAction {

	public static final String PARAM_COMPONENT_TYPE = "component-type";
	public static final String COMPONENT_TYPE_ALL = "all";
	public static final String COMPONENT_TYPE_TODO = "todo";
	public static final String COMPONENT_TYPE_EVENT = "event";
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		SearchCalPropsForm queryForm = (SearchCalPropsForm)form;
		String componentType = request.getParameter(PARAM_COMPONENT_TYPE);
		// if invalid value passed assume all
        PIMServiceLocal pim = PIMServiceUtil.getLocalHome().create();
		if((componentType == null)||(isInvalidType(componentType)))
		{
			componentType = COMPONENT_TYPE_ALL;
		}
		
		// load tasks
		if(componentType.equals(COMPONENT_TYPE_ALL)||componentType.equals(COMPONENT_TYPE_TODO))
		{
			 
		} 
		// load events
		if(componentType.equals(COMPONENT_TYPE_ALL)||componentType.equals(COMPONENT_TYPE_EVENT))
		{
			//DateFormat dtFormat = GMTTimeFormat.getFormat();
		//	
		}
		WCAPSession session = this.getSession(request);
        String address = pim.getUserName();
        request.setAttribute("address", address);
		request.setAttribute("calinfo",new CalInfo(session.getUserId(),session.getUserName(),session.getUserId(),null,null));
		return this.findForward(mapping, "success", request);
	}

	private boolean isInvalidType(String type)
	{
		String test = type.toLowerCase().trim();
		if(test.equals(COMPONENT_TYPE_ALL)) return false;
		if(test.equals(COMPONENT_TYPE_TODO)) return false;
		if(test.equals(COMPONENT_TYPE_EVENT)) return false;
		return true;
	}
}