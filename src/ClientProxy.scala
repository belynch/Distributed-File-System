import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.Socket

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
	
	def read(file : String){
		//setup
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
		//if the requested file does no exist, the server IP field will be empty
		var temp = message.split(":")
		if(temp.length > 1){
			fileServerIP = temp(1)
			message = sIn.readLine()
			fileServerport = message.split(":")(1).toInt
			message = sIn.readLine()
			fileUID = message.split(":")(1).toInt
		}
		
		println("IP: " + fileServerIP + "\nPort: " + fileServerport + "\nUID: " + fileUID)
		
		//terminate connection with directory server
		sOut.println("DISCONNECT")
		sOut.flush()
		socket.close()
		
		//setup connection with file server
		//socket = new Socket(fileServerIP, fileServerport)
		//sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
		//sOut = new PrintStream(socket.getOutputStream())
	
		//request file from file server
		//sOut.println("READ:" + fileUID)
		//sOut.flush()
		
		//receive file
		
		
		//terminate connection with file server

	}
	
	def modify(file : String){
	
	}
	
	def write(file : String){
		
		//setup
		connect(directoryIP, directoryPort)

		var fileServerIP : String = ""
		var fileServerport : Int = -1
		var fileUID : Int = -1
		
		var message : String = ""
		
		//send WRITE command to directory server
		sOut.println("WRITE:" + file)
		sOut.flush()
		
		//receive file location from directory server
		message = sIn.readLine()
		//if the requested file does no exist, the server IP field will be empty
		var temp = message.split(":")
		if(temp.length > 1){
			fileServerIP = temp(1)
			fileServerIP = message.split(":")(1)
			message = sIn.readLine()
			fileServerport = message.split(":")(1).toInt
			message = sIn.readLine()
			fileUID = message.split(":")(1).toInt
		}
		
		println("IP: " + fileServerIP + "\nPort: " + fileServerport + "\nUID: " + fileUID)
		
		//terminate connection with directory server
		sOut.println("DISCONNECT")
		sOut.flush()
		socket.close()
		
		//setup connection with file server
		//socket = new Socket(fileServerIP, fileServerport)
		//sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
		//sOut = new PrintStream(socket.getOutputStream())
	
		//send file to file server
		
		//terminate connection with file server
	}
	
}