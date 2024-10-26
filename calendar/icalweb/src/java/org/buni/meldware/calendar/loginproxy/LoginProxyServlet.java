/*
 * JBoss Calendar Server.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.buni.meldware.calendar.loginproxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Login Proxy will proxy the login via basic Authentication to the Form
 * Authenticated site. The idea: 1. use the Tomcat supported cross web-app
 * authentication. 2. send a redirect to the real page.
 * 
 * @author aron
 */
public class LoginProxyServlet extends HttpServlet {
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String targetURL = request.getRequestURL().toString().replaceFirst(
				"lp/basic", "moses");
		response.sendRedirect(targetURL);
	}
}
