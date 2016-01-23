import java.io.{BufferedReader, InputStreamReader, PrintStream, PrintWriter}
import java.net.Socket
import java.io.{File}
import scala.io.Source

class ClientProxy(){
	
	val READ : String = "READ:"
	val WRITE : String = "WRITE:"

	val directoryIP : String = "localhost"
	val directoryPort : Int = 7000
	
	var socket : Socket = null
	var sIn : BufferedReader = null
	var sOut : PrintStream = null
	
	val cache : Cache = new Cache()
	var message : String = ""
	
	var fileServerIP : String = ""
	var fileServerport : Int = -1
	var fileUID : Int = -1
		
	/**
	 *
	 * Initialises a TCP connection with a given IP and port number
	 *
	**/
	def connect(ip : String, port : Int){
		try{
			socket = new Socket(ip, port)
			sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
			sOut = new PrintStream(socket.getOutputStream())
		}
		catch{
			case notConnected : java.net.ConnectException => println("Can't connect to: " + directoryIP + " on port: "+ directoryPort)
		}
	}
	
	/**
	 *
	 * Sends the disconnect command to the server and closes the socket
	 *
	**/
	def disconnect(){
		sOut.println("DISCONNECT")
		sOut.flush()
		socket.close()
	}
	
	/**
	 *
	 * Reads a file from the file system
	 *
	**/
	def read(file : String){
		
		fileServerIP = ""
		fileServerport = -1
		fileUID = -1
		
		println("\nREAD")
		println("Connecting to directory server")
		connect(directoryIP, directoryPort)
		var fileExists = getFileID(file, READ)
		//terminate connection with directory server
		disconnect()
			
		if(fileExists){
			//setup connection with file server
			println("Connecting to file server")
			connect(fileServerIP,fileServerport)
			val receivedFile = getFile(fileUID, file)
			//terminate connection with file server
			disconnect()	
		}
		else{
			println("invalid file")
		}
	}
	
	
	/**
	 * 
	 * Writes a new file to the file system
	 *
	**/
	def write(path : String){
		
		fileServerIP = ""
		fileServerport = -1
		fileUID = -1
		
		//convert file to list of strings for transmission
		val lines = Source.fromFile(path).getLines().toList
		
		println("WRITE")
		println("Connecting to directory server")
		connect(directoryIP, directoryPort)
		var fileExists = getFileID(path, WRITE)
		//terminate connection with directory server
		disconnect()
			
		if(fileExists){
			//setup connection with file server
			println("Connecting to file server")
			connect(fileServerIP,fileServerport)
			writeFile(fileUID, lines, WRITE)
			//terminate connection with file server
			disconnect()	
		}
		else{
			println("invalid file")
		}
	}
	
	/**
	 * 
	 * Gets the file id of a given filename from the directory server
	 *
	**/
	def getFileID(file : String, messageType : String): Boolean = {
		var result: Boolean = false
		//send READ command to directory server
		sOut.println(messageType + file)
		sOut.flush()
		
		//receive file location from directory server
		message = sIn.readLine()
		//if the requested file does no exist, the data field will be empty
		var temp = message.split(":") 		//messages of the form   NAME:DATA
		if(temp.length > 1){ 				//length will be 2 if data field is not empty
			fileServerIP = temp(1)
			message = sIn.readLine()
			fileServerport = message.split(":")(1).toInt
			message = sIn.readLine()
			fileUID = message.split(":")(1).toInt
			
			println("IP: " + fileServerIP + "\nPort: " + fileServerport + "\nUID: " + fileUID)
			result = true;
		}
		else{
			result = false
		}
		return result
	}
	
	/**
	 * 
	 * Sends the lines of a file to the file server - stored using the provided
	 * file id
	 *
	**/
	def writeFile(fileUID:Int, lines:List[String], messageType : String){
		sOut.println(messageType + fileUID)
		//write the file line by line
		for(l <- lines){
			sOut.println(l)
			println(l)
		} 
		sOut.println("END;")
		sOut.flush()
		
		//receive response: success/failure
		message = sIn.readLine()
		println(message)
	}
	
	/**
	 * 
	 * Gets a file from the file server given a file id
	 *
	**/
	def getFile(fileUID:Int , fileName:String ): File = {
		//request file from file server
		sOut.println("READ:" + fileUID)
		sOut.flush()
		
		//receive file name
		message = sIn.readLine()
		println(message)
		
		val receivedFile = new File(fileName)
		val writer = new PrintWriter(receivedFile)
		
		//receive file contents line by line, and write each line to a new file
		println("receiving file contents")
		
		message = sIn.readLine()
		while(message != "END;"){
		  writer.write(message + "\n")
		  writer.flush()
		  println(message)
		  message = sIn.readLine()
		}
		writer.close()	
		
		//terminate connection with file server
		disconnect()
		return receivedFile
	}
}