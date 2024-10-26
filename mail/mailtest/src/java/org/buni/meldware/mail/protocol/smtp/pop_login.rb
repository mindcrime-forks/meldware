connect($host,$port)
expect "\\+OK.*$"
send   "USER " + $user
expect "\\+OK.*$"
send   "PASS " + $pass
expect "\\+OK.*$"
send   "QUIT"
expect "\\+OK.*$"
disconnect