import java.io.{File}

class Cache(){
	
	var files : List[CacheEntry] = List()
	
	/**
	 *
	 * Adds a file to the cache
	 *
	**/
	def addFile(file : File, stamp : Int){
		files = new CacheEntry(file, stamp)  :: files
	}
	
	/**
	 *
	 * Retrieves a file from the cache
	 *
	**/
	def retrieveFile(filePath : String) : CacheEntry = {
		var temp : CacheEntry = null
		if(contains(filePath) != -1) {
			for(f <- files){
				if(f.filePath == filePath){
					return f
				} 
			}
		}
		return temp
	}
	
	/**
	 *
	 * Update cache entry
	 *
	**/
	def updateFile(file: File, filePath : String, state : Int){
		var entry = retrieveFile(filePath)
		entry.file = file
		entry.filePath = file.getPath()
		entry.state = state
	}
	
	/**
	 *
	 * Removes a file from the cache
	 *
	**/
	def removeFile(filePath : String){
		var updatedFiles : List[CacheEntry] = List()
		for(f <- files){
			if(f.filePath != filePath){
				updatedFiles = f :: updatedFiles
			} 
		}
		files = updatedFiles
	}
	
	/**
	 *
	 * Returns the state of the entry if it is contained within the cache
	 * otherwise returns -1
	 *
	**/
	def contains(filePath : String) : Int = {
		if(!isEmpty()){
			for(f <- files){
				if(f.filePath == filePath){
					return f.state
				} 
			}
		}
		return -1
	}
	
	/**
	 *
	 * Returns true if the cache is empty
	 *
	**/
	def isEmpty() : Boolean = {
		return files.isEmpty
	}
}