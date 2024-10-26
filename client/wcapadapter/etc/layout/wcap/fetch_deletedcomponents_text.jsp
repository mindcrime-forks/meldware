<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/wcap.tld" prefix="wcap" %>
<%@ page import="org.buni.meldware.Version"%>
BEGIN:VCALENDAR
PRODID:-//<%=Version.CAL_SERVER_PRODID%>//EN
METHOD:PUBLISH
VERSION:2.0
X-NSCP-CALPROPS-LAST-MODIFIED:20060814T114426Z
X-NSCP-CALPROPS-CREATED:20060814T114303Z
X-NSCP-CALPROPS-READ:999
X-NSCP-CALPROPS-WRITE:999
X-NSCP-CALPROPS-RELATIVE-CALID:<bean:write name="calinfo" property="calId"/>
X-NSCP-CALPROPS-NAME:<bean:write name="calinfo" property="name"/>
X-NSCP-CALPROPS-PRIMARY-OWNER:<bean:write name="calinfo" property="ownerId"/>
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@@o^c^WDEIC^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@@o^a^RSF^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^a^frs^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^c^^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^p^r^g
X-NSCP-CALPROPS-RESOURCE:0
X-S1CS-CALPROPS-ALLOW-DOUBLEBOOKING:1
<logic:present name="invites"><logic:iterate id="invite" name="invites"><wcap:iCalVEvent invite="invite" deleted="true"/></logic:iterate></logic:present>
X-NSCP-WCAP-ERRNO:0
END:VCALENDAR
