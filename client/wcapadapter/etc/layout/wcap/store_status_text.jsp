<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/wcap.tld" prefix="wcap" %>
<%@ page import="org.buni.meldware.Version"%>
BEGIN:VCALENDAR
PRODID:-//<%=Version.CAL_SERVER_PRODID%>//EN
METHOD:PUBLISH
VERSION:2.0
X-NSCP-WCAP-ERRNO:0
BEGIN:VEVENT
REQUEST-STATUS:<bean:write name="status" property="ICalStatus"/>
UID:<bean:write name="status" property="invite.event.GUID"/>
END:VEVENT
END:VCALENDAR