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
	
	def retrieveFile(filePath : String) : CacheEntry = {
		var temp : CacheEntry = null
		if(cacheContains(filePath)) {
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
	 * Returns true if the specified file is contained within the cache
	 *
	**/
	def cacheContains(filePath : String) : Boolean = {
		if(!isEmpty()){
			for(f <- files){
				if(f.filePath == filePath){
					return true
				} 
			}
		}
		return false
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