# Java SSH2 Communication With DeltaTau Power PMAC 

Java wrapper to connect with Power PMAC, initialize the gpascii text interpreter, set parameter values, and get parameter values.  


# Dependencies

### [Apache Commons Lang](http://commons.apache.org/proper/commons-lang/)

To bring this into you project (e.g., Eclipse), right-click the package in the package explorer -> build path -> add external libraires and include the [.jar](http://commons.apache.org/proper/commons-lang/download_lang.cgi)

### [Jsch](http://www.jcraft.com/jsch/)

To bring this into you project (e.g., Eclipse), right-click the package in the package explorer -> build path -> add external libraires and include the [.jar](http://www.jcraft.com/jsch/)

# Documentation

See javadoc in [doc/](doc)

# Examples

See [test/TestDeltaTauComm.java](test/TestDeltaTauComm.java)

# Methods

### gpasciiCommand()

Use this method to send a “set” command to the Power PMAC, e.g., “DestCS2X=100.3”.  Set commands always contain “=”

### gpasciiQuery()

Use this method to send a “get” command to the Power PMAC, e.g., "DestCS2X", and receive the value as a `String`

### close()

Use this method to close the SSH session