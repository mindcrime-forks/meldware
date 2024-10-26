<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ taglib uri="/WEB-INF/wcap.tld" prefix="wcap" %><%@ page import="org.buni.meldware.Version"%><?xml version="1.0" encoding="UTF-8"?>
<iCalendar>
<iCal version="2.0" prodid="-//<%=Version.CAL_SERVER_PRODID%>//EN" METHOD="PUBLISH">
<X-NSCP-CALPROPS-LAST-MODIFIED>20061102T170639Z</X-NSCP-CALPROPS-LAST-MODIFIED>
<X-NSCP-CALPROPS-CREATED>20060814T110002Z</X-NSCP-CALPROPS-CREATED>
<X-NSCP-CALPROPS-READ>999</X-NSCP-CALPROPS-READ>
<X-NSCP-CALPROPS-WRITE>999</X-NSCP-CALPROPS-WRITE>
<X-NSCP-CALPROPS-RELATIVE-CALID><bean:write name="calinfo" property="calId"/></X-NSCP-CALPROPS-RELATIVE-CALID>
<X-NSCP-CALPROPS-NAME><bean:write name="calinfo" property="name"/></X-NSCP-CALPROPS-NAME>
<X-NSCP-CALPROPS-PRIMARY-OWNER><bean:write name="calinfo" property="ownerId"/></X-NSCP-CALPROPS-PRIMARY-OWNER>
<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@@o^c^WDEIC^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>
<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@@o^a^RSF^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>
<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@^a^rsf^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>
<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@^c^^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>
<X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>@^p^r^g</X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY>
<X-NSCP-CALPROPS-RESOURCE>0</X-NSCP-CALPROPS-RESOURCE>
<X-S1CS-CALPROPS-ALLOW-DOUBLEBOOKING>1</X-S1CS-CALPROPS-ALLOW-DOUBLEBOOKING>
<FREEBUSY>
<START><bean:write name="startDate"/></START>
<END><bean:write name="endDate"/></END>
<logic:present name="invites"><logic:iterate id="invite" name="invites"><FB FBTYPE="BUSY"><wcap:UTCDate name="invite" property="event.startDate"/>/<wcap:UTCDate name="invite" property="event.endDate"/></FB></logic:iterate></logic:present>
</FREEBUSY>
<X-NSCP-WCAP-ERRNO>0</X-NSCP-WCAP-ERRNO>
</iCal>
</iCalendar>

