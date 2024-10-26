package org.buni.meldware.wcapadapter.action;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.buni.meldware.calendar.data.Address;
import org.buni.meldware.calendar.interfaces.PIMServiceLocal;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.common.preferences.UserProfile;
import org.buni.meldware.common.preferences.UserProfileService;
import org.buni.meldware.wcapadapter.bean.Error;
import org.buni.meldware.wcapadapter.form.LoginForm;
import org.buni.meldware.wcapadapter.session.WCAPSession;
import org.buni.meldware.wcapadapter.session.WCAPSessionStore;
import org.jboss.security.auth.callback.UsernamePasswordHandler;

public class Login extends WCAPAction {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
        loadServices(mapping);
		// TODO Auto-generated method stub
		LoginForm loginForm = (LoginForm) form;

		try {
			UsernamePasswordHandler handler = new UsernamePasswordHandler(loginForm
					.getUser(), loginForm.getPassword());
			LoginContext lc = new LoginContext("client-login", handler);
			lc.login();

			PIMServiceLocal pim = PIMServiceUtil.getLocalHome().create();
			String address = pim.getUserAddress();
			pim.remove();
			UserProfile prof = ups.findProfile(pim.getUserAddress());
			WCAPSession session = WCAPSessionStore.getInstance().createSession(
					loginForm.getUser(), prof.getUsername(),  //TODO ACO make full name
					loginForm.getPassword());

			request.setAttribute("wcapsession", session);

			lc.logout();
			return this.findForward(mapping, "success", request);
		} catch (LoginException e) {
			return this.findError(mapping, Error.LOGIN_FAILED, request);
		}
	}

}
