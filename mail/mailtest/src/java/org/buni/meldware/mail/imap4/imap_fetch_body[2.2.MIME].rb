expect '. OK.*$'
send   '1 login "tom" "tom"'
expect '1 OK.*'
send   '2 select "INBOX"'
scan   '2 OK.*$'
send   '3 fetch 5 body[2.2.MIME]'
expect '\* 5 FETCH \(body\[2\.2\.MIME\].*$'
scan   '3 OK.*$'
send   '4 logout'
expect '4 OK.*$'
