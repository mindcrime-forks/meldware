/*
 * JBoss Calendar Server.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.buni.meldware.calendar.web.icalendar;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.buni.meldware.calendar.interfaces.ICalSyncServiceLocal;
import org.buni.meldware.calendar.interfaces.ICalSyncServiceUtil;
import org.buni.meldware.calendar.interfaces.PIMServiceUtil;
import org.buni.meldware.calendar.util.HibernateLookUp;

/**
 * This Servlet supports HTTP Put unlike Struts(main reason it is not an
 * action). The Servlet can generate as well as parse ICalendar files.
 * 
 * @author $Author: andy $
 * @version $Revision: 1.3 $
 */
public class ICalendarServlet extends HttpServlet {
	static final long serialVersionUID = "$Id: ICalendarServlet.java,v 1.3 2007/12/31 05:16:09 andy Exp $".hashCode(); //$NON-NLS-1$

	private Log log = LogFactory.getLog(ICalendarServlet.class);
	
	public ICalendarServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
            HibernateLookUp.checkSchema();
			//PIMServiceUtil.getLocalHome().create().initApp();
		} catch (Exception e) {
			log.error("Failed to init app", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log("SERVING HTTP GET");
		this.generateICalendar(request, response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log("SERVING HTTP POST");
		this.generateICalendar(request, response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log("SERVING HTTP PUT");
		this.parseICalendar(request, response);
	}

	public void generateICalendar(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			ICalSyncServiceLocal ical = ICalSyncServiceUtil.getLocalHome()
					.create();
			Calendar vCalendar = ical.getICalendar();
			CalendarOutputter output = new CalendarOutputter();
			response.setContentType("text/calendar");
			output.output(vCalendar, response.getOutputStream());
		} catch (Exception exp) {
			log("failed to load schedule", exp);
			return;
		}
	}

	public void parseICalendar(HttpServletRequest request,
			HttpServletResponse response) {
		Calendar vCalendar = null;
		CalendarBuilder calbuilder = new CalendarBuilder();
		try {
			vCalendar = calbuilder.build(request.getInputStream());
			ICalSyncServiceLocal ical = ICalSyncServiceUtil.getLocalHome()
					.create();
			ical.publishCalendar(vCalendar);
		} catch (Exception e) {
			log("Calendar parser failed", e);
		}
	}

}
