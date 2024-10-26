def send(message)
  $ph.send(message)
end

def expect(pattern)
  $ph.expect(pattern)
end

def sendFile(message)
  $ph.sendFile(message)
end

def dumpFile(message)
  $ph.dumpFile(message)
end

def scan(pattern)
  $ph.scan(pattern)
end

def getNext
  $ph.next()
end

def getCurrent
  $ph.current()
end

def fail(test, message)
  if (test)
    $ph.fail(message)
  end
end

def connect(host, port)
  $ph.connect(host, port)
end

def disconnect
  $ph.disconnect()
end
