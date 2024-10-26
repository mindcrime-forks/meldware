<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ page import="org.buni.meldware.Version"%>
BEGIN:VCALENDAR
PRODID:-//<%=Version.CAL_SERVER_PRODID%>//EN
METHOD:PUBLISH
VERSION:2.0
X-NSCP-WCAP-SESSION-ID:<bean:write name="wcapsession" property="sessionId"/>
X-NSCP-WCAP-USER-ID:<bean:write name="wcapsession" property="userId"/>
X-NSCP-WCAP-CALENDAR-ID:<bean:write name="wcapsession" property="userId"/>
X-NSCP-WCAP-ERRNO:0
END:VCALENDAR