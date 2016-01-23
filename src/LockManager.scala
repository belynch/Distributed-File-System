import java.io.{File}

/**
 *
 * 
 * 
**/
class LockManager(){
	
	var files : List[LockEntry]  = List()
	
	/**
	 * Adds a new file entry to the files list
	**/
	def addEntry(fileUID : String){
		files = new LockEntry(fileUID) :: files
	}
	
	/**
	 * Removes a file entry 
	 * ie. this.deleteEntry(2)
	**/
	def deleteEntry(id : String){
		var updatedFiles: List[LockEntry] = List()
		for(f <- files){
			if(f.id != id){
				updatedFiles = f :: updatedFiles
			} 
		}
		files = updatedFiles
	}
	

	/**
	 * Searches the files for a given id and returns the
	 * file entry if it exists.
	 * ie. this.getEntry(2)
	**/
	def getEntry(id : String) : LockEntry = {
		val entry : LockEntry = null
		for(f <- files){
			if(f.id == id){
				return f
			} 
		}
		return entry
	}
	
	/**
	 * Identifies if a sspecified file exists
	**/
	def entryExists(id : String) : Boolean = {
		if(!isEmpty()){
			for(f <- files){
				if(f.id == id){
					return true
				} 
			}
		}
		return false
	}
	
	/**
	 * Identifies if the list of files is empty or not
	**/
	def isEmpty() : Boolean = {
		return files.isEmpty
	}
	
	
}