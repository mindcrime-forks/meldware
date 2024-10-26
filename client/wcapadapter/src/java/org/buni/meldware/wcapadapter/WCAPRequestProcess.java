package org.buni.meldware.wcapadapter;
import java.io.IOException;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.buni.meldware.wcapadapter.action.WCAPSessionAction;
import org.buni.meldware.wcapadapter.form.WCAPForm;
import org.buni.meldware.wcapadapter.session.WCAPSession;
import org.buni.meldware.wcapadapter.session.WCAPSessionStore;
import org.jboss.security.auth.callback.UsernamePasswordHandler;

public class WCAPRequestProcess extends RequestProcessor {

	public static final String INVALID_SESSION = "invalid-session";

	public static final String SOL = "SorryOutOfLuck";

	@Override
	protected ActionForward processActionPerform(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm form,
			ActionMapping mapping) throws IOException, ServletException {
		WCAPSession session = null;
		LoginContext lc = null;
		if (WCAPSessionAction.class.isAssignableFrom(action.getClass())) {
			if(form == null)
				throw new ServletException("OOPS the form is null for: " + mapping.getName());
			WCAPForm sessionForm = (WCAPForm) form;
			session = WCAPSessionStore.getInstance().getSession(
					sessionForm.getId());
			if(session==null)
				return mapping.findForward(INVALID_SESSION);
			
			UsernamePasswordHandler handler = new UsernamePasswordHandler(
					session.getUserId(), session.getPassword());
			
			try {
				lc = new LoginContext("client-login", handler);
				lc.login();
			} catch (LoginException e) {
				return mapping.findForward(INVALID_SESSION);
			}
			
			request.setAttribute(WCAPSessionAction.SESSION_KEY, session);
		}
		try {
			return super.processActionPerform(request, response, action, form,
					mapping);
		} catch (Throwable e) {
			// something bad happened
			throw new ServletException(e);
		} finally {
			if (session != null) {
				try {
					lc.logout();
				} catch (LoginException e) {
					return mapping.findForward(SOL);
				}
			}
		}
	}

}
