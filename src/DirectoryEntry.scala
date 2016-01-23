/**
 *
 * A directory entry which contains the human readable directory and name of the file and 
 * the details of the file server in which the file is located on
 * 
**/
class DirectoryEntry(parent: String, name: String, host: String, port : Int, id : Int){
	
	val relative_distinguished_name : String = name
	var distinguished_name : String = parent + name
	var parent_directory : String = parent
	
	var host_name : String = host
	var host_port : Int = port
	var host_id : Int = id
	var state : Int = 0
	
	
	def setHostName(name : String){
		host_name = name
	}
	
	def setHostPort(port : Int){
		host_port = port
	}
	
	def setHostId(id : Int){
		host_id = id
	}
	
	def setDistinguishedName(parent : String){
		distinguished_name = parent + "/" + relative_distinguished_name
	}
	
	def updateState(){
		state = state + 1
	}
}