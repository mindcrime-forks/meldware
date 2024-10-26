<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ taglib uri="/WEB-INF/wcap.tld" prefix="wcap" %><%@ page import="org.buni.meldware.Version"%><?xml version="1.0" encoding="UTF-8"?>
<iCalendar>
<iCal version="2.0" prodid="-//<%=Version.CAL_SERVER_PRODID%>//EN" METHOD="PUBLISH">
<X-NSCP-WCAP-ERRNO><bean:write name="wcaperror" property="errorCode"/></X-NSCP-WCAP-ERRNO>
</iCal>
</iCalendar>