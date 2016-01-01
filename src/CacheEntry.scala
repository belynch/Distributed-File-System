import java.io.{File}

class CacheEntry(newFile: File, timeStamp: Int){
	
	var file : File = newFile
	var stamp : Int = timeStamp
	var filePath : String = file.getPath()

}