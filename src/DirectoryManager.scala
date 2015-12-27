/**
 *
 * A Directory service which contains a list of directory entries
 * 
**/
class DirectoryManager(){
	
	var entries : List[DirectoryEntry]  = List()
	// the next available unique identifier for a file
	var current_id : Int = 0
	
	/**
	 * Adds a new entry to the directory and incremenets the value of current_id
	 * current_id is the unique identifier of the file on the file server
	**/
	def addEntry(parent: String, name: String, host: String, port : Int){
		entries = new DirectoryEntry(parent, name, host, port, current_id) :: entries
		current_id += 1
	}
	
	/**
	 * Removes an entry from the directory with a given distinguished name.
	 * ie. this.deleteEntry("root/test/test.txt")
	**/
	def deleteEntry(distinguished_name : String){
		var updatedEntries: List[DirectoryEntry] = List()
		for(e <- entries){
			if(e.distinguished_name != distinguished_name){
				updatedEntries = e :: updatedEntries
			} 
		}
		entries = updatedEntries
	}
	
	/**
	 * Searches the directory for a given distinguished name and returns the
	 * entry if it exists.
	 * ie. this.searchEntries("root/test/test.txt")
	**/
	def searchEntries(distinguished_name : String) : DirectoryEntry = {
		val entry : DirectoryEntry = null
		for(e <- entries){
			if(e.distinguished_name == distinguished_name){
				return e
			} 
		}
		return entry
	}
	
	def getId() : Int = {
		return current_id
	}
	
}