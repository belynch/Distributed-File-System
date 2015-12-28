# Distributed-File-System

Overview
---------


Features
---------


Protocol
---------

###CLIENT <-> DIRECTORY SERVER

read request
```
READ: [String]\n
```
read response
```
SERVER IP: [String]\n
SERVER PORT: [Int]\n
FILE UID: [Int]\n
```
Location = empty string if invalid read request


write request
```
WRITE: [String]\n
```
write response
```
SERVER IP: [String]\n
SERVER PORT: [Int]\n
FILE UID: [Int]\n
```
Location = empty string if invalid write request


list request
```
LS: [String]
```

list response
```
NO. ITEMS: [Int]\n
ITEM.1: [String]\n

ITEM.N: [String]\n
```

###CLIENT <-> FILE SERVER

####LOCK SERVICE

lock request
```
LOCK: [String]
```
lock response
```
SUCCESS: [Bool]
```

unlock request
```
UNLOCK: [String]
```
unlock response
```
SUCCESS: [Bool]
```