def sendHeader(host)
  send "Host: #{host}"
  send "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.0.10) Gecko/20070221 Thunderbird/1.5.0.10"
  send "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
  send "Accept-Language: en-gb,en;q=0.5"
  send "Accept-Encoding: gzip,deflate"
  send "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7"
  send "Keep-Alive: 300"
  send "Connection: keep-alive"
  send "Pragma: no-cache"
  send "Cache-Control: no-cache"
  send ""
end