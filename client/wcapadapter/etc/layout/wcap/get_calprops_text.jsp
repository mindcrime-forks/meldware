<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ page import="org.buni.meldware.Version"%><?xml version="1.0" encoding="UTF-8"?>
BEGIN:VCALENDAR

PRODID:-//<%=Version.CAL_SERVER_PRODID%>//EN
METHOD:PUBLISH
VERSION:2.0
X-NSCP-CALPROPS-LAST-MODIFIED:20060814T113039Z
X-NSCP-CALPROPS-CREATED:20060814T111204Z
X-NSCP-CALPROPS-READ:999
X-NSCP-CALPROPS-WRITE:999
X-NSCP-CALPROPS-RELATIVE-CALID:<bean:write name="address"/>
X-NSCP-CALPROPS-NAME:
X-NSCP-CALPROPS-PRIMARY-OWNER:<bean:write name="address"/>
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@@o^c^WDEIC^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@@o^a^RSF^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^a^frs^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^c^^g
X-NSCP-CALPROPS-ACCESS-CONTROL-ENTRY:@^p^r^g
X-NSCP-CALPROPS-RESOURCE:0
X-S1CS-CALPROPS-ALLOW-DOUBLEBOOKING:1
X-S1CS-CALPROPS-FB-INCLUDE:1
X-S1CS-CALPROPS-COMMON-NAME:<bean:write name="address"/>
X-NSCP-WCAP-ERRNO:0
END:VCALENDAR