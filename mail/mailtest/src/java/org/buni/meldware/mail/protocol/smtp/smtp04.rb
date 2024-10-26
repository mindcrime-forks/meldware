expect "^220 .*$"
send   "HELO localhost"
expect "^250 .*$"
send   "MAIL FROM:<tom@localhost>"
expect "^250 .*$"
send   "RCPT TO:<tom@localhost>"
expect "^250 .*$"
send   "DATA"
expect "^354 .*$"
send   <<MAIL_BLOCK
From tom@localhost Sun May 28 15:36:28 2006
Subject: test
From: Test <tom@localhost>
To: tom@localhost
Content-Type: text/plain
Message-Id: <1148826987.3485.2.camel@corona>
Mime-Version: 1.0
X-Mailer: Evolution 2.6.1 
Date: Sun, 28 May 2006 15:36:28 +0100
X-Evolution-Format: text/plain
X-Evolution-Account: 1148751105.3500.0@corona
X-Evolution-Transport: smtp://tom;auth=PLAIN@localhost:9025/;use_ssl=never
X-Evolution-Fcc: mbox:/home/mike/.evolution/mail/local#Sent

This is a test mail message
.
MAIL_BLOCK
expect "^250 .*$"
send   "QUIT"
expect "^221 .*$"