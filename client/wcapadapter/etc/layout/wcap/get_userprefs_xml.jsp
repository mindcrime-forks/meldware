<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %><%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %><%@ page import="org.buni.meldware.Version"%><?xml version="1.0" encoding="UTF-8"?>
<iCalendar>
<iCal version="2.0" prodid="-//<%=Version.CAL_SERVER_PRODID%>//EN">
<X-NSCP-WCAP-PREF-cn>
</X-NSCP-WCAP-PREF-cn>
<X-NSCP-WCAP-PREF-givenName>
</X-NSCP-WCAP-PREF-givenName>
<X-NSCP-WCAP-PREF-mail>
<bean:write name="address"/>
</X-NSCP-WCAP-PREF-mail>
<X-NSCP-WCAP-PREF-preferredlanguage>
en
</X-NSCP-WCAP-PREF-preferredlanguage>
<X-NSCP-WCAP-PREF-sn>
</X-NSCP-WCAP-PREF-sn>
<X-NSCP-WCAP-PREF-icsCalendar>
<bean:write name="address"/>
</X-NSCP-WCAP-PREF-icsCalendar>
<X-NSCP-WCAP-PREF-icsTimezone>
GMT
</X-NSCP-WCAP-PREF-icsTimezone>
<X-NSCP-WCAP-PREF-icsDefaultSet>

</X-NSCP-WCAP-PREF-icsDefaultSet>
<X-NSCP-WCAP-PREF-icsFirstDay>
2
</X-NSCP-WCAP-PREF-icsFirstDay>
<X-NSCP-WCAP-PREF-icsSet>

</X-NSCP-WCAP-PREF-icsSet>
<X-NSCP-WCAP-PREF-icsSubscribed>
<bean:write name="address"/>$
</X-NSCP-WCAP-PREF-icsSubscribed>
<X-NSCP-WCAP-PREF-icsFreeBusy>
<bean:write name="address"/>
</X-NSCP-WCAP-PREF-icsFreeBusy>
<X-NSCP-WCAP-PREF-ceDefaultView>
weekview
</X-NSCP-WCAP-PREF-ceDefaultView>
<X-NSCP-WCAP-PREF-ceInterval>
PT0H30M
</X-NSCP-WCAP-PREF-ceInterval>
<X-NSCP-WCAP-PREF-ceFontFace>
PrimaSans BT,Verdana,sans-serif
</X-NSCP-WCAP-PREF-ceFontFace>
<X-NSCP-WCAP-PREF-ceDateOrder>
D/M/Y
</X-NSCP-WCAP-PREF-ceDateOrder>
<X-NSCP-WCAP-PREF-ceDateSeparator>
.
</X-NSCP-WCAP-PREF-ceDateSeparator>
<X-NSCP-WCAP-PREF-ceClock>
24
</X-NSCP-WCAP-PREF-ceClock>
<X-NSCP-WCAP-PREF-ceDayTail>
18
</X-NSCP-WCAP-PREF-ceDayTail>
<X-NSCP-WCAP-PREF-ceExcludeSatSun>
1
</X-NSCP-WCAP-PREF-ceExcludeSatSun>
<X-NSCP-WCAP-PREF-ceGroupInviteAll>
1
</X-NSCP-WCAP-PREF-ceGroupInviteAll>
<X-NSCP-WCAP-PREF-ceSingleCalendarTZID>
0
</X-NSCP-WCAP-PREF-ceSingleCalendarTZID>
<X-NSCP-WCAP-PREF-ceAllCalendarTZIDs>
1
</X-NSCP-WCAP-PREF-ceAllCalendarTZIDs>
<X-NSCP-WCAP-PREF-ceNotifyEnable>
1
</X-NSCP-WCAP-PREF-ceNotifyEnable>
<X-NSCP-WCAP-PREF-ceNotifyEmail>
none
</X-NSCP-WCAP-PREF-ceNotifyEmail>
<X-NSCP-WCAP-PREF-ceDefaultAlarmStart>
P15M
</X-NSCP-WCAP-PREF-ceDefaultAlarmStart>
<X-NSCP-WCAP-PREF-ceDefaultAlarmEmail>
none
</X-NSCP-WCAP-PREF-ceDefaultAlarmEmail>
<X-NSCP-WCAP-PREF-ceColorSet>
pref_group_7
</X-NSCP-WCAP-PREF-ceColorSet>

<X-NSCP-WCAP-PREF-ceToolText>
1
</X-NSCP-WCAP-PREF-ceToolText>
<X-NSCP-WCAP-PREF-ceToolImage>
1
</X-NSCP-WCAP-PREF-ceToolImage>
<X-NSCP-WCAP-PREF-nswcalCALID>

</X-NSCP-WCAP-PREF-nswcalCALID>
<X-NSCP-WCAP-PREF-icsDWPHost>

</X-NSCP-WCAP-PREF-icsDWPHost>
<X-NSCP-WCAP-PREF-icsCalendarOwned>
<bean:write name="address"/>$
</X-NSCP-WCAP-PREF-icsCalendarOwned>
<X-NSCP-WCAP-SERVER-PREF-allowchangepassword>
no
</X-NSCP-WCAP-SERVER-PREF-allowchangepassword>
<X-NSCP-WCAP-SERVER-PREF-allowcreatecalendars>
yes
</X-NSCP-WCAP-SERVER-PREF-allowcreatecalendars>
<X-NSCP-WCAP-SERVER-PREF-allowdeletecalendars>
yes
</X-NSCP-WCAP-SERVER-PREF-allowdeletecalendars>
<X-NSCP-WCAP-SERVER-PREF-allowpublicwritablecalendars>
yes
</X-NSCP-WCAP-SERVER-PREF-allowpublicwritablecalendars>
<X-NSCP-WCAP-SERVER-PREF-format>
text/calendar
</X-NSCP-WCAP-SERVER-PREF-format>
<X-NSCP-WCAP-SERVER-PREF-freebusybegin>
30
</X-NSCP-WCAP-SERVER-PREF-freebusybegin>
<X-NSCP-WCAP-SERVER-PREF-freebusyend>
30
</X-NSCP-WCAP-SERVER-PREF-freebusyend>
<X-NSCP-WCAP-SERVER-PREF-validateowners>
no
</X-NSCP-WCAP-SERVER-PREF-validateowners>
<X-NSCP-WCAP-SERVER-PREF-version>
3.1
</X-NSCP-WCAP-SERVER-PREF-version>
<X-NSCP-WCAP-ERRNO>0</X-NSCP-WCAP-ERRNO>
</iCal>
</iCalendar>
