# Distributed-File-System


###Protocol
___

####CLIENT - DIRECTORY SERVER

*Read request*
```
READ: [String]\n
```

*Read response*
```
SERVER IP: [String]\n
SERVER PORT: [Int]\n
FILE UID: [Int]\n
```
Location = empty string if invalid read request


*Write request*
```
WRITE: [String]\n
```

*Write response*
```
SERVER IP: [String]\n
SERVER PORT: [Int]\n
FILE UID: [Int]\n
```
Location = empty string if invalid write request


*List request*
```
LS: [String]
```

*List response*
```
NO. ITEMS: [Int]\n
ITEM.1: [String]\n

ITEM.N: [String]\n
```
___
####CLIENT - FILE SERVER


*Lock request*
```
LOCK: [String]
```

*Lock response*
```
SUCCESS: [Bool]
```

*Unlock request*
```
UNLOCK: [String]
```

Unlock response
```
SUCCESS: [Bool]
```