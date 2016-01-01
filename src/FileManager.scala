import java.io.{File}

/**
 *
 * A File manager which ...
 * 
**/
class FileManager(){
	
	var files : List[FileEntry]  = List()
	
	/**
	 * Adds a new file entry to the files list
	**/
	def addEntry(file: File, id : Int){
		files = new FileEntry(file, id) :: files
	}
	
	/**
	 * Removes a file entry 
	 * ie. this.deleteEntry(2)
	**/
	def deleteEntry(id : Int){
		var updatedFiles: List[FileEntry] = List()
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
	def getEntry(id : Int) : FileEntry = {
		val entry : FileEntry = null
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
	def entryExists(id : Int) : Boolean = {
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