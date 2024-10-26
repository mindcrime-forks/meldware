<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ page import="org.buni.meldware.Version"%><?xml version="1.0" encoding="UTF-8"?>
<iCalendar>
<iCal version="2.0" prodid="-//<%=Version.CAL_SERVER_PRODID%>//EN" METHOD="PUBLISH">
<EVENT>
<UID><bean:write name="status" property="invite.event.GUID"/></UID>
<RSTATUS><bean:write name="status" property="ICalStatus"/></RSTATUS>
</EVENT>
<X-NSCP-WCAP-ERRNO>0</X-NSCP-WCAP-ERRNO>
</iCal>
</iCalendar>