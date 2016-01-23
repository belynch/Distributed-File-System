import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter, PrintStream, ByteArrayInputStream}
import java.net.{Socket, ServerSocket, SocketException}
import scala.io.BufferedSource

/**
 *
 * Worker thread class
 *
**/
class DirectoryServerListener(socket:Socket, serverInterface:DirectoryServerInterface) extends Runnable {
	
	var sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
	var sOut : PrintStream = new PrintStream(socket.getOutputStream())
	
	var message = ""
	val IPaddress = socket.getLocalAddress().toString().drop(1)
	
	/**
	 * Receives messages over a given socket and invokes the corresponding handler.
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
					else if(message.startsWith("WRITE")){
						handleWrite(message)
					}
					else if(message.startsWith("DISCONNECT")){
						handleDisconnect()
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
	
	/**
	 *
	 * Sends an error message over the connection
	 *
	**/
	def error(code:Int, description:String){
		sOut.println("ERROR_CODE: " + code
						+ "\nERROR_DESCRIPTION: " + description)
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
		val file = message.split(":")(1)
		
		if(serverInterface.fileExists(file)) {
			sOut.println("SERVER IP:" + serverInterface.getFileServerIP 
							+ "\nSERVER PORT:" + serverInterface.getFileServerPort
							+ "\nFILE UID:" + serverInterface.getFileUID(file)
							+ "\nFILE STATE:" + serverInterface.getFileState(file))
			sOut.flush()
			println("READ::UID: " + serverInterface.getFileUID(file))
		}
		else{
			//invalid read
			sOut.println("SERVER IP:" + "" 
							+ "\nSERVER Port:" + ""
							+ "\nFILE UID:" + ""
							+ "\nFILE STATE:" + "")
			sOut.flush()
		}
	}
	
	/**
	 *
	 * Handler for write command
	 *
	**/
	def handleWrite(message:String){
		val file = message.split(":")(1)
		
		if(serverInterface.writeFile(file)){
			sOut.println("SERVER IP:" + serverInterface.getFileServerIP 
							+ "\nSERVER Port:" + serverInterface.getFileServerPort
							+ "\nFILE UID:" + serverInterface.getFileUID(file)
							+ "\nFILE STATE:" + serverInterface.getFileState(file))
			sOut.flush()
			println("WRITE::UID: " + serverInterface.getFileUID(file))
		}
		else{
			//file already exists, return info to overwrite it
			sOut.println("SERVER IP:" + serverInterface.getFileServerIP 
							+ "\nSERVER Port:" + serverInterface.getFileServerPort
							+ "\nFILE UID:" + serverInterface.getFileUID(file)
							+ "\nFILE STATE:" + serverInterface.getFileState(file))
			sOut.flush()
			//client is going to write to the file so update its state
			serverInterface.updateFileState(file)
		}
	}
	
	/**
	 *
	 * Handler for disconnect command
	 *
	**/
	def handleDisconnect() {
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
	
}