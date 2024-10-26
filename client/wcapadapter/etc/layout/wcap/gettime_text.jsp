<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ page import="org.buni.meldware.Version"%>
BEGIN:VCALENDAR
PRODID:-//<%=Version.CAL_SERVER_PRODID%>//EN
VERSION:2.0
X-NSCP-WCAPTIME:<bean:write name="currentTime"/>
X-NSCP-WCAP-ERRNO:0
END:VCALENDAR
