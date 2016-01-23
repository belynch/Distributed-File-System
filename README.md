# Distributed-File-System
___
###Overview
___
The distributed file system is comprised of a Directory Server, File Server and Client Proxy. 
In order do identify the location of a file, the client must communicate with the direcctory 
service to identify UID of the file and the IP and port number of the file server which has requested 
file. The client then communicates with the file server using this information. The Client Proxy 
provides a scala interface for the client and manages any caching.

___
###Description
___
####Directory Server
The Directory Server contains a DirectoryManager which stores all file infromation as DirectoryEntry objects.
The file name, file path and file location(file server ip/port/UID) are stored. The Directory Server handles two 
file commands: read and write. For each command, given the filepath of the file, it returns the corresponding file 
location information. In the case of the write command, if the file doesn't already exist, it creates a new file 
UID which in turn will be used by the file server when the client connects to it.

####File Server
The File Server contains a FileManager which stores all files as FileEntry objects. Each FileEntry object stores
the file, the file path and locking information. The File Server handles to file commands: read and write. For the
read command, given the file UID, the File Server will return the file contents. For the write command, given the file
UID, the File Server will create a new FileEntry if the file UID doesn't already exist, otherwise it will overwrite
the contents of the existing FileEntry with the new file.

####Caching
Caching is done on the client side, implemented within the ClientProxy. The Directory Server maintains a state for
each file(a logical clock). The reason for this is that when retrieving a file UID from the Directory Server, reads and 
writes are distinguished. Because of this, when a file is written to, the state can be updated. When a file is read from, 
the standard read command is sent to the Directory Server, which returns the state of the file along with the other 
information. The ClientProxy can then compare this state to the state of its own cached file and proceed accordingly.
If the file is cached and up to date, the client doesn't connect to the File Server(but still connects to the Directory
Server).

####Locking
Locking is implemented using a Lock Server. When a client wishes to read or write to a file they first contact the Lock 
Server to make sure the file isn't locked. If it is locked, the command will fail. The Lock Server doesn't maintain a list
of all the files and their lock state, but instead holds a list of currently locked files. If a client acquires the lock on
a file, that file path is stored by the Lock Server. When they release the lock, the stored filepath is deleted by the 
server and so that file can be accessed by all, or locked by the next client to attempt to acquire it. 
___
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
FILE STATE: [Int]\n
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
FILE STATE: [Int]\n
```
Directory server assigns new file UID and location to write the file

___
###LOCK SERVER

Access file:
```
ACCESS: [fileUID : Int]
```

Access response:
```
LOCK STATUS: [Int]
```
1 if locked, 0 if unlocked

Acquire lock:
```
ACQUIRE: [fileUID : Int]
```

Acquire response:
```
SUCCESS: [Boolean]
```

Release lock:
```
RELEASE: [fileUID : Int]
```
___
###FILE SERVER

Read file:
```
READ: [fileUID : Int]\n
```

Read response:
```
FILE NAME: [Int]\n
FILE CONTENTS: [List[String]]\n
EOF
```

Write file:
```
WRITE: [fileUID : Int]\n
FILE CONTENTS: [List[String]]\n
EOF
```

Write response:
```
SUCCESS: [Boolean]
```
___
###Running
___
```
sh compile.sh
sh directoryServer.sh 7000
sh fileServer.sh 8000
sh lockServer.sh 9000
sh clientTest.sh
```
The above port numbers are hardcoded into the tests, as well as the server IP addresses(localhost)