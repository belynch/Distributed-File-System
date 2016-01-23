# Distributed-File-System

The distributed file system is comprised of a Directory Server, File Server and Client Proxy. 
In order do identify the location of a file, the client must communicate with the direcctory 
service to identify UID of the file and the IP and port number of the file server which has requested 
file. The client then communicates with the file server using this information. The Client Proxy 
provides a scala interface for the client and manages any caching.

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

Read file:
```
READ: [UID]\n
```

Read response:
```
FILE NAME: [UID]\n
FILE CONTENTS: [List[String]]\n
EOF
```

Write file:
```
WRITE: [UID]\n
FILE CONTENTS: [List[String]]\n
EOF
```

Write response:
```
SUCESS: [Boolean]
```