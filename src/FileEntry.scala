import java.io.{File}

/**
 *
 * A file entry which ....
 * 
**/
class FileEntry(file_name : File, unique_identifier : Int){
	
	var file : File = file_name
	val id : Int = unique_identifier
	var locked : Bool = false
	
	
	def lock(){
		locked = true
	}
	
	def unlock(){
		locked = false
	}
	
}