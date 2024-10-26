load "#{$path}/wcap_common.rb"
connect($host,$port)

send "GET /login.wcap?fmt-out=text%2Fcalendar&user=#{$user}&password=#{$pass} HTTP/1.1"
sendHeader($host)
expect "HTTP/1.1 200.*"
scan   "BEGIN:VCALENDAR"
expect "PRODID:-//Sun/Calendar Server//EN"
scan   "X-NSCP-WCAP-SESSION-ID:(.*)"
getCurrent =~ /X-NSCP-WCAP-SESSION-ID:(.*)/
sessionId = $1
scan   "X-NSCP-WCAP-CALENDAR-ID:jdoe"
scan   "END:VCALENDAR"

send "GET /get_all_timezones.wcap?appid=mozilla-calendar&fmt-out=text%2Fcalendar&id=#{sessionId} HTTP/1.1"
sendHeader($host)
expect "HTTP/1.1 200.*"
scan "BEGIN:VCALENDAR"
scan "END:VCALENDAR"

send "GET /get_freebusy.wcap?appid=mozilla-calendar&calid=#{$user}&busyonly=1&dtstart=20070312T000000Z&dtend=20070316T000000Z&fmt-out=text%2Fxml&id=#{sessionId} HTTP/1.1"
sendHeader($host)
expect "HTTP/1.1 200.*"
scan "<iCalendar>"
scan "</iCalendar>"

send "GET /storeevents.wcap?appid=mozilla-calendar&calid=#{$user}&storetype=1&attendees=&fetch=1&relativealarm=1&compressed=1&recurring=1&orgUID=#{$user}&priority=0&replace=1&icsClass=PUBLIC&status=3&attachments=&categories=&summary=Test%20appointment&desc=&location=TBA&icsUrl=&dtend=20070312T131500Z&transparent=0&dtstart=20070312T121500Z&tzid=UTC&alarmStart=&alarmPopup=&alarmEmails=&contacts=20070311T162228Z%3A%3A&rrules=&rdates=&exrules=&exdates=&fmt-out=text%2Fcalendar&id=#{sessionId} HTTP/1.1"
sendHeader($host)
scan "BEGIN:VCALENDAR"
scan "END:VCALENDAR"

disconnect