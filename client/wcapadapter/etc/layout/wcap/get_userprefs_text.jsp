<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ page import="org.buni.meldware.Version"%><?xml version="1.0" encoding="UTF-8"?>
BEGIN:VCALENDAR
PRODID:-///<%=Version.CAL_SERVER_PRODID%>//EN
X-NSCP-WCAP-PREF-cn:
X-NSCP-WCAP-PREF-givenName:
X-NSCP-WCAP-PREF-mail:<bean:write name="address"/>
X-NSCP-WCAP-PREF-preferredlanguage:en
X-NSCP-WCAP-PREF-sn:
X-NSCP-WCAP-PREF-icsCalendar:<bean:write name="address"/>
X-NSCP-WCAP-PREF-icsTimezone:GMT
X-NSCP-WCAP-PREF-icsDefaultSet:
X-NSCP-WCAP-PREF-icsFirstDay:2
X-NSCP-WCAP-PREF-icsSubscribed:<bean:write name="address"/>$
X-NSCP-WCAP-PREF-icsFreeBusy:<bean:write name="address"/>
X-NSCP-WCAP-PREF-ceDefaultView:weekview
X-NSCP-WCAP-PREF-ceInterval:PT0H30M
X-NSCP-WCAP-PREF-ceFontFace:PrimaSans BT,Verdana,sans-serif
X-NSCP-WCAP-PREF-ceDateOrder:D/M/Y
X-NSCP-WCAP-PREF-ceDateSeparator:.
X-NSCP-WCAP-PREF-ceClock:24
X-NSCP-WCAP-PREF-ceDayTail:18
X-NSCP-WCAP-PREF-ceExcludeSatSun:1
X-NSCP-WCAP-PREF-ceGroupInviteAll:1
X-NSCP-WCAP-PREF-ceSingleCalendarTZID:0
X-NSCP-WCAP-PREF-ceAllCalendarTZIDs:1
X-NSCP-WCAP-PREF-ceNotifyEnable:1
X-NSCP-WCAP-PREF-ceNotifyEmail:none
X-NSCP-WCAP-PREF-ceDefaultAlarmStart:P15M
X-NSCP-WCAP-PREF-ceDefaultAlarmEmail:none
X-NSCP-WCAP-PREF-ceColorSet:pref_group_7
X-NSCP-WCAP-PREF-ceToolText:1
X-NSCP-WCAP-PREF-ceToolImage:1
X-NSCP-WCAP-PREF-nswcalCALID:
X-NSCP-WCAP-PREF-icsDWPHost:
X-NSCP-WCAP-PREF-icsCalendarOwned:<bean:write name="address"/>$
X-NSCP-WCAP-SERVER-PREF-allowchangepassword:no
X-NSCP-WCAP-SERVER-PREF-allowcreatecalendars:yes
X-NSCP-WCAP-SERVER-PREF-allowdeletecalendars:yes
X-NSCP-WCAP-SERVER-PREF-allowpublicwritablecalendars:yes
X-NSCP-WCAP-SERVER-PREF-format:text/xml
X-NSCP-WCAP-SERVER-PREF-freebusybegin:30
X-NSCP-WCAP-SERVER-PREF-freebusyend:30
X-NSCP-WCAP-SERVER-PREF-validateowners:no
X-NSCP-WCAP-SERVER-PREF-version:3.1
X-NSCP-WCAP-ERRNO:0
END:VCALENDAR
