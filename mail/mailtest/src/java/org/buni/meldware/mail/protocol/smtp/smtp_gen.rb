connect($host,$port)
expect "^220 .*$"
send   "HELO localhost"
expect "^250 .*$"
send   "MAIL FROM:" + $address
expect "^250 .*$"
send   "RCPT TO:" + $address
expect "^250 .*$"
send   "DATA"
expect "^354 .*$"
dumpFile $filename
send   "."
expect "^250 .*$"
send   "QUIT"
expect "^221 .*$"
disconnect
