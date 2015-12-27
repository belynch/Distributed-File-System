import java.io.{File, FileOutputStream}

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
		var fileEntry = new FileEntry(file, id) :: files
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
	 * Searches the files list for a given id and returns the
	 * entry if it exists.
	 * ie. this.searchEntries(2)
	**/
	def searchEntries(id : Int) : FileEntry = {
		val entry : FileEntry = null
		for(f <- files){
			if(f.id == id){
				return f
			} 
		}
		return entry
	}
	
}