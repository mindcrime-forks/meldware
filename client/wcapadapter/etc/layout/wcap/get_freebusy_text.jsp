<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ taglib uri="/WEB-INF/wcap.tld" prefix="wcap" %><%@ page import="org.buni.meldware.Version"%>
BEGIN:VCALENDAR
PRODID:-//<%=Version.CAL_SERVER_PRODID%>//EN
METHOD:PUBLISH
VERSION:2.0
X-NSCP-CALPROPS-LAST-MODIFIED:20061102T170639Z
X-NSCP-CALPROPS-CREATED:20060814T110002Z
X-NSCP-CALPROPS-READ:999
X-NSCP-CALPROPS-WRITE:999
X-NSCP-CALPROPS-RELATIVE-CALID:<bean:write name="calinfo" property="calId"/>
X-NSCP-CALPROPS-NAME:<bean:write name="calinfo" property="name"/>
X-NSCP-CALPROPS-PRIMARY-OWNER:<bean:write name="calinfo" property="ownerId"/>
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@@o^c^WDEIC^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@@o^a^RSF^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^a^rsf^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^c^^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^p^r^g
X-NSCP-CALPROPS-RESOURCE:0
X-S1CS-CALPROPS-ALLOW-DOUBLEBOOKING:1
BEGIN:VFREEBUSY
DTSTART:<bean:write name="startDate"/>
DTEND:<bean:write name="endDate"/>
<logic:present name="invites"><logic:iterate id="invite" name="invites">FREEBUSY;FBTYPE=BUSY:<wcap:UTCDate name="invite" property="event.startDate"/>/<wcap:UTCDate name="invite" property="event.endDate"/>
</logic:iterate></logic:present>END:VFREEBUSY
X-NSCP-WCAP-ERRNO:0
END:VCALENDAR
