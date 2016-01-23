import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter, PrintStream, ByteArrayInputStream}
import java.net.{Socket, ServerSocket, SocketException}
import scala.io.BufferedSource

/**
 *
 * Worker thread class
 *
**/
class LockServerListener(socket:Socket, serverInterface:LockServerInterface) extends Runnable {
	
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
					
					if(message.startsWith("ACCESS")){
						handleAccess(message)
					}
					else if(message.startsWith("ACQUIRE")){
						handleAcquire(message)
					}
					else if(message.startsWith("RELEASE")){
						handleRelease(message)
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
	 * Handler for accesss command
	 *
	**/
	def handleAccess(message : String) {
		val fileUID = message.split(":")(1)
		
		//if file is locked already, fail to access
		if(serverInterface.isLocked(fileUID)){
			sOut.println("LOCK STATUS:" + 1)
			sOut.flush()
			println("ACCESS::locked")
		}
		else{
			sOut.println("LOCK STATUS:" + 0)
			sOut.flush()
			println("ACCESS::unlocked")
		}
	}
	
	/**
	 *
	 * Handler for acquire command
	 *
	**/
	def handleAcquire(message : String) {
		val fileUID = message.split(":")(1)
		
		//if file is locked already, fail to acquire
		if(serverInterface.isLocked(fileUID)){
			sOut.println("SUCCESS:" + false)
			sOut.flush()
			println("ACQUIRE::locked")
		}
		else{
			serverInterface.lockFile(fileUID)
			sOut.println("SUCCESS:" + true)
			sOut.flush()
			println("ACQUIRE::unlocked - locking file")
		}
	}
	
	/**
	 *
	 * Handler for release command
	 *
	**/
	def handleRelease(message : String) {
		val fileUID = message.split(":")(1)
		
		//if file is locked, release lock
		if(serverInterface.isLocked(fileUID)){
			serverInterface.unlockFile(fileUID)
			println("RELEASE::unlocked - file removed")
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