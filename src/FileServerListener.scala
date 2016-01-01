import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{Socket, ServerSocket, SocketException}
import scala.io.BufferedSource
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
		//send file

		if(serverInterface.fileExists(fileUID.toInt)){
			sOut.println("READ SUCCESS")
			sOut.flush()
		}
		else{
			println("READ ERROR: file UID: " + fileUID + " not found")
			sOut.println("READ FAILED: " + fileUID)
			sOut.flush()
		}
	}
	
	/**
	 *
	 * Handler for modify command
	 *
	**/
	def handleModify(message:String){
		val fileUID = message.split(":")(1)
		//send file and wait to receive modified file
		
		if(serverInterface.fileExists(fileUID.toInt)){
			sOut.println("MODIFY SUCESS")
			sOut.flush()
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
		val fileUID = message.split(":")(1)
		//receive file and add entry

		val test : File = new File("test/test.txt")
		
		if(!serverInterface.fileExists(fileUID.toInt)){
			serverInterface.writeFile(test, fileUID.toInt)
			sOut.println("WRITE SUCCESS: " + fileUID)
			sOut.flush()
		}
		else{
			println("WRITE ERROR: file UID already exists")
			sOut.println("WRITE FAILED: " + fileUID)
		}
	}
	
	def hadnleDisconnect() {
		sOut.println("DISCONNECTED FROM DIRECTORY SERVER"
						+ "\nIP:" + IPaddress 
						+ "\nPort:" + serverInterface.getPort)
		sOut.flush()				
		sOut.close
		sIn.close
		socket.close
	}
	
	def handleKill(){
		serverInterface.shutdown()
		socket.close()
	}
	
}