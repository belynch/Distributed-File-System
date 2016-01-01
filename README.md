# Distributed-File-System

The distributed file system is comprised of a Directory Server, File Server and Client Proxy. 
In order do identify the location of a file, the client must communicate with the direcctory 
service to identify the server IP and port number of the file server containing the requested 
file, and the UID of the file on that server. The client then communicates with the file server 
using this information. The Client Proxy provides a scala interface for use by the client and 
manages the caching of files.

###Protocol
___

####DIRECTORY SERVER

Read request:
```
READ: [filePath]\n
```

Read response:
```
SERVER IP: [String]\n
SERVER PORT: [Int]\n
FILE UID: [Int]\n
```
All fields will be empty if the file doesn't exist


Modify request:
```
MODIFY: [filePath]\n
```

Modify response:
```
SERVER IP: [String]\n
SERVER PORT: [Int]\n
FILE UID: [Int]\n
```
All fields will be empty if the file doesn't exist


Write request:
```
WRITE: [filePath]\n
```

Write response:
```
SERVER IP: [String]\n
SERVER PORT: [Int]\n
FILE UID: [Int]\n
```
All fields will be empty if the file doesn't exist



___
###FILE SERVER

Read request:
```
READ: [UID]\n
```

Read response:
```
FILE NAME: [UID]\n
FILE CONTENTS: [String]\n
EOF
```

Modify request:
```
MODIFY: [UID]\n
```

Modify response to client:
```
FILE NAME: [String]\n
FILE CONTENTS: [String]\n
EOF
```
client response to server:
```
MODIFIED FILE CONTENTS: [String]\n
EOF
```

Modify termination from server:
```
SUCESS: [Boolean]
```

Write request:
```
WRITE: [UID]\n
FILE CONTENTS: [String]\n
EOF
```

Write response:
```
SUCESS: [Boolean]
```