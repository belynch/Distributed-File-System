import java.io.{File}
import scala.io.{Source}

/**
 *
 * A file entry which ....
 * 
**/
class FileEntry(file_name : File, unique_identifier : Int){
	
	var file : File = file_name
	val id : Int = unique_identifier
	
	/**
	 * Converts the file contents to string
	**/
	def fileToList(): List[String] ={
      return Source.fromFile(file.getPath()).getLines().toList
	}
	
	def setFile(file : File){
		this.file = file
	}
}