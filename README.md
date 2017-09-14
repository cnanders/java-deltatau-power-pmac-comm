# Java SSH2 Communication With DeltaTau Power PMAC 

Java wrapper to connect with Power PMAC, initialize the gpascii text interpreter, set parameter values, and get parameter values.  


# Dependencies

[Jsch](http://www.jcraft.com/jsch/)

# Documentation

See javadoc in [/doc/](doc)

# Examples

See [test/TestDeltaTauComm.java](test/TestDeltaTau.java)

# Methods

### gpasciiCommand()

Use this method to send a “set” command to the Power PMAC, e.g., “DestCS2X=100.3”.  Set commands always contain “=”

### gpasciiQuery()

Use this method to send a “get” command to the Power PMAC, e.g., "DestCS2X", and receive the value as a `String`

### close()

Use this method to close the SSH session