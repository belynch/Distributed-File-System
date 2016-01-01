import java.io.{BufferedReader, InputStreamReader, PrintStream, PrintWriter}
import java.net.{Socket, ServerSocket, SocketException}
import scala.io.{Source}
import java.io.{File}

/**
 *
 * Worker thread class
 *
**/
class FileServerListener(socket:Socket, serverInterface:FileServerInterface) extends Runnable {

	//val sOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")))
	
	var sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
	var sOut : PrintStream = new PrintStream(socket.getOutputStream())
	
	var message = ""
	val IPaddress = socket.getLocalAddress().toString().drop(1)
	
	/**
	 * Receives messages over a given socket and handles responses.
	**/
	def run(){
		println("THREAD " + Thread.currentThread().getId()+": running")
		try {
			while(!socket.isClosed()){
				if(socket.getInputStream().available() > 0){
					message = ""
					message = sIn.readLine()
					println("\nReceived message: " + message)
					
					if(message.startsWith("HELO")){
						handleHelo(message)
					}
					else if(message.startsWith("READ")){
						handleRead(message)
					}
					else if(message.startsWith("MODIFY")){
						handleModify(message)
					}
					else if(message.startsWith("WRITE")){
						handleWrite(message)
					}
					else if(message.startsWith("DISCONNECT")){
						hadnleDisconnect()
					}
					else if(message == "KILL_SERVICE"){
						handleKill()
					}					
					else{
						println("unhandled message type")
					}
				}
			}
		}
		catch {
			case e: SocketException => println("THREAD " + Thread.currentThread().getId()+" SocketException: worker run function failed")
		}
	}

	
	def error(code:Int, description:String){
		sOut.println("ERROR_CODE: " + code
						+ "\nERROR_DESCRIPTION: " + description)
		sOut.flush()
	}
	
	/**
	 *
	 * Handler for helo command
	 *
	**/
	def handleHelo(message:String){
		sOut.println(message + "\nIP:" + IPaddress 
								+"\nPort:" + serverInterface.getPort)
		sOut.flush()
	}
	
	/**
	 *
	 * Handler for read command
	 *
	**/
	def handleRead(message:String){
		val fileUID = message.split(":")(1)
		

		if(serverInterface.fileExists(fileUID.toInt)){
			//send file
			val lines = serverInterface.getFile(fileUID.toInt).fileToList()
			
			println("sending file contents")
			sOut.println("FILE:" + fileUID)
			for(l <- lines){
				sOut.println(l)
				println(l)
			} 
			sOut.println("END;")
			sOut.flush()
			println("READ SUCCESS")
			
		}
		else{
			println("READ ERROR: file UID: " + fileUID + " not found")
		}
	}
	
	/**
	 *
	 * Handler for modify command
	 *
	**/
	def handleModify(message:String){
		val fileUID = message.split(":")(1)
		 
		if(serverInterface.fileExists(fileUID.toInt)){
			//send file
			val lines = serverInterface.getFile(fileUID.toInt).fileToList()
			
			println("sending file contents")
			sOut.println("FILE:" + fileUID)
			for(l <- lines){
				sOut.println(l)
				println(l)
			} 
			sOut.println("EOF")
			sOut.flush()
			println("SEND SUCCESS")
			
			//and wait to receive modified file
			val modifiedFile = new File(fileUID + ".txt")
			val writer = new PrintWriter(modifiedFile)
			
			//receive modified file contents line by line, and write each line to a new file
			var input = ""
			println("receiving file")
			
			input = sIn.readLine()
			while(input != "EOF"){
			  writer.write(input + "\n")
			  writer.flush()
			  println(input)
			  input = sIn.readLine()
			}
			writer.close()
			
			//send success response
			sOut.println("MODIFY SUCCESSFUL")
			sOut.flush()
			
			//overwrite file entry with modified file
			serverInterface.getFile(fileUID.toInt).setFile(modifiedFile)
		}
		else{
			println("MODIFY ERROR: file UID: " + fileUID + " not found")
		}	
	}
	
	/**
	 *
	 * Handler for write command
	 *
	**/
	def handleWrite(message:String){
		//receive file UID
		val fileUID = message.split(":")(1)
		
		val file = new File(fileUID + ".txt")
		val writer = new PrintWriter(file)
		
		//receive file contents line by line, and write each line to a new file
		var input = ""
		println("receiving file")
		
		while(input != "END;"){
          input = sIn.readLine()
		  writer.write(input + "\n")
		  writer.flush()
		  //println(input)
        }
		writer.close()
				
		if(!serverInterface.fileExists(fileUID.toInt)){
			serverInterface.writeFile(file, fileUID.toInt)
			sOut.println("WRITE SUCCESS: " + fileUID)
			sOut.flush()
		}
		else{
			println("WRITE ERROR: file UID already exists")
			sOut.println("WRITE FAILED: " + fileUID)
		}
	}
	
	/**
	 *
	 * Handler for disconnect command
	 *
	**/
	def hadnleDisconnect() {
		sOut.println("DISCONNECTED FROM DIRECTORY SERVER"
						+ "\nIP:" + IPaddress 
						+ "\nPort:" + serverInterface.getPort)
		sOut.flush()				
		sOut.close
		sIn.close
		socket.close
	}
	
	/**
	 *
	 * Handler for kill service command
	 *
	**/
	def handleKill(){
		serverInterface.shutdown()
		socket.close()
	}
	
	/**
	 *
	 * Converts a list of strings to a file
	 *
	**/
	def listToFile(fileName : String, input : List[String]){
		val file = new File(fileName)
		val writer = new PrintWriter(file)
		for (line <- input) {
		  println("Contents: " + line)
		  if(line != ""){
			writer.write(line + "\n")
			writer.flush()
		  }
		}
		writer.close()
	}
}