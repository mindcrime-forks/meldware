files = []
files[0] = "mail/mailtest/etc/layout/text-mail.msg"
files[1] = "mail/mailtest/etc/layout/text-mail.msg"
files[2] = "mail/mailtest/etc/layout/text-mail.msg"
files[3] = "mail/mailtest/etc/layout/text-mail.msg"
files[4] = "mail/mailtest/etc/layout/text-mail.msg"
files[5] = "mail/mailtest/etc/layout/text-mail.msg"
files[6] = "mail/mailtest/etc/layout/single-image-attached.msg"
files[7] = "mail/mailtest/etc/layout/single-image-attached.msg"
files[8] = "mail/mailtest/etc/layout/single-image-attached.msg"
files[9] = "mail/mailtest/etc/layout/test2.eml"


0.upto($count - 1) do |i|
  connect($host,$port)
  expect "^220 .*$"
  send   "HELO localhost"
  expect "^250 .*$"
  send   "MAIL FROM:" + $address
  expect "^250 .*$"
  send   "RCPT TO:" + $address
  expect "^250 .*$"
  idx = i % files.length
#  puts "#{idx} #{files[idx]}"
#  $stdout.flush
  send   "DATA"
  expect "^354 .*$"
  dumpFile files[idx]
  send   "."
  expect "^250 .*$"
  send   "QUIT"
  expect "^221 .*$"
  disconnect
end