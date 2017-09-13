# Java SSH2 Communication With DeltaTau Power PMAC 

Java wrapper to connect with Power PMAC, initialize the gpascii text interpreter, set parameter values, and get parameter values.  


# Dependencies

[Jsch](http://www.jcraft.com/jsch/)

# Use

See test/TestDeltaTauComm.java

### gpasciiInit()

Call this one time to initialize `com.jcraft.jsch.Session`, creates a shell `Channel` within the `Session`, writes the `gpascii -2` command to the `OutputBuffer` of the `Channel` and then reads the `InputBuffer` of the `Channel` until it recieves the "STDIN Open for ASCII input" response

### gpasciiCommand()

Use this to send a “set” command to the Power PMAC, e.g., “DestCS2X=100.3”.  Set commands always contain “=”

### gpasciiQuery()

Use this to send a “get” command to the Power PMAC, e.g., "DestCS2X", and receive the value as a `String`