import java.io.{BufferedReader, InputStreamReader, PrintStream, PrintWriter}
import java.net.Socket
import java.io.{File}
import scala.io.Source

class ClientProxy(){
	
	val directoryIP : String = "localhost"
	val directoryPort : Int = 7000
	
	var socket : Socket = null
	var sIn : BufferedReader = null
	var sOut : PrintStream = null
	
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
	
	def disconnect(){
		sOut.println("DISCONNECT")
		sOut.flush()
		socket.close()
	}
	
	/**
	 *
	 *
	**/
	def read(file : String){
		//setup
		println("\nREAD")
		println("Connecting to directory server")
		connect(directoryIP, directoryPort)

		var fileServerIP : String = ""
		var fileServerport : Int = -1
		var fileUID : Int = -1
		
		var message : String = ""
		
		//send READ command to directory server
		sOut.println("READ:" + file)
		sOut.flush()
		
		//receive file location from directory server
		message = sIn.readLine()
		//if the requested file does no exist, the server IP field will be empty and we will not connect
		//to the file server
		var temp = message.split(":") //messages of the form   NAME:DATA
		if(temp.length > 1){ //length will be 2 if data field is not empty
			fileServerIP = temp(1)
			message = sIn.readLine()
			fileServerport = message.split(":")(1).toInt
			message = sIn.readLine()
			fileUID = message.split(":")(1).toInt
			
			println("IP: " + fileServerIP + "\nPort: " + fileServerport + "\nUID: " + fileUID)
		
			//terminate connection with directory server
			disconnect()
			
			//setup connection with file server
			println("Connecting to file server")
			connect(fileServerIP,fileServerport)
			
			//request file from file server
			sOut.println("READ:" + fileUID)
			sOut.flush()
			
			//receive file name
			message = sIn.readLine()
			println(message)
			
			val receivedFile = new File(file)
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
		}
		else{
			println("invalid file")
			disconnect()
		}	
	}
	
	/**
	 *
	 *
	**/
	def modify(file : String){
		//setup
		println("\nMODIFY")
		println("Connecting to directory server")
		connect(directoryIP, directoryPort)

		var fileServerIP : String = ""
		var fileServerport : Int = -1
		var fileUID : Int = -1
		
		var message : String = ""
		
		//send MODIFY command to directory server
		sOut.println("MODIFY:" + file)
		sOut.flush()
		
		//receive file location from directory server
		message = sIn.readLine()
		//if the requested file does no exist, the server IP field will be empty and we will not connect
		//to the file server
		var temp = message.split(":") //messages of the form   NAME:DATA
		if(temp.length > 1){ //length will be 2 if data field is not empty
			fileServerIP = temp(1)
			message = sIn.readLine()
			fileServerport = message.split(":")(1).toInt
			message = sIn.readLine()
			fileUID = message.split(":")(1).toInt
			
			println("IP: " + fileServerIP + "\nPort: " + fileServerport + "\nUID: " + fileUID)
		
			//terminate connection with directory server
			disconnect()
			
			//setup connection with file server
			println("Connecting to file server")
			connect(fileServerIP,fileServerport)
		
			//request file from file server
			sOut.println("MODIFY:" + fileUID)
			sOut.flush()
			
			//receive file name
			message = sIn.readLine()
			println(message)
			
			val receivedFile = new File(file)
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
			
			////////////////////
			// Modify file
			////////////////////
			
			//send file back
			val lines = Source.fromFile(file).getLines().toList
			//send file to file server
			println("sending file contents")
			sOut.println("WRITE:" + fileUID)
			for(l <- lines){
				sOut.println(l)
				println(l)
			} 
			sOut.println("EOF")
			sOut.flush()
			
			//receive response: success/failure
			message = sIn.readLine()
			println(message)
	
			//terminate connection with file server
			disconnect()
		}
		else{
			println("invalid file")
			disconnect()
		}	
	}
	
	/**
	 *
	 *
	**/
	def write(path : String){
		println("WRITE")
		//convert file to list of strings for transmission
		val lines = Source.fromFile(path).getLines().toList

		//setup
		println("Connecting to directory server")
		connect(directoryIP, directoryPort)

		var fileServerIP : String = ""
		var fileServerport : Int = -1
		var fileUID : Int = -1
		
		var message : String = ""
		
		//send WRITE command to directory server
		sOut.println("WRITE:" + path)
		sOut.flush()
		
		//receive file location from directory server
		message = sIn.readLine()
		//if the requested file does no exist, the server IP field will be empty
		var temp = message.split(":") //messages of the form   NAME:DATA
		if(temp.length > 1){
			fileServerIP = temp(1) 
			fileServerIP = message.split(":")(1)
			message = sIn.readLine()
			fileServerport = message.split(":")(1).toInt
			message = sIn.readLine()
			fileUID = message.split(":")(1).toInt
			
			println("IP: " + fileServerIP + "\nPort: " + fileServerport + "\nUID: " + fileUID)
		
			//terminate connection with directory server
			disconnect()
			
			//setup connection with file server
			connect(fileServerIP,fileServerport)
		
			//send file to file server
			println("sending file contents")
			sOut.println("WRITE:" + fileUID)
			for(l <- lines){
				sOut.println(l)
				println(l)
			} 
			sOut.println("END;")
			sOut.flush()
			
			//receive response: success/failure
			message = sIn.readLine()
			println(message)
			
			//terminate connection with file server
			disconnect()
		}
		else{
			println("invalid file")
			sOut.println("DISCONNECT")
			sOut.flush()
			socket.close()
		}
	}
}