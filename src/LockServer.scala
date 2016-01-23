import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter}
import java.net.{Socket, ServerSocket, SocketException}
import java.util.concurrent.{Executors}
import scala.io.BufferedSource

trait LockServerInterface {
  def getPort: String
  
  def lockFile(fileUID : String)
  def unlockFile(fileUID : String)
  def isLocked(fileUID : String): Boolean
  
  def shutdown(): Unit
}

/**
 *
 * Object which implements a Directory Server. A directory Server contains a 
 * DreictoryManager used in the management of the directory space
 *
**/
object LockServer extends LockServerInterface {

	val pool = java.util.concurrent.Executors.newFixedThreadPool(20)
	var serverSocket: ServerSocket = null
	var port: Int = 0
	val lockManager : LockManager =  new LockManager()
	
	/**
	 *
	 * Main function which initialises and runs the server
	 *
	**/
	def main (args: Array[String]){
		port = args(0).toInt
		serverSocket = new ServerSocket(port)
		
		run()
	}
	
	/**
	 * Server run function
	 *
	 * Receives incomming connections over a server socket and passes them to workers
	**/
	def run(){
		try {
			println("Server starting on port: " + port)
			//loop until a worker receives a message to kill the service
			while(!serverSocket.isClosed){
				try {
					pool.execute(new LockServerListener(serverSocket.accept(), this))
				}
				catch {
					case e: SocketException => println("SocketException: accepting new connection failed")
				}	
			}
		}
		finally {
			pool.shutdown()
		}
	}
	
	def lockFile(fileUID : String){
		//if the fileUID is stored, then the file is locked
		if(!lockManager.entryExists(fileUID)){
			lockManager.addEntry(fileUID)
		}
		else{
			//file is locked
		}
	}
	
	def unlockFile(fileUID : String){
		//if the fileUID is stored, then the file is locked
		if(lockManager.entryExists(fileUID)){
			lockManager.deleteEntry(fileUID)
		}
		else{
			//invalid file unlock - file isn't locked
		}
	}
	
	def isLocked(fileUID : String): Boolean = {
		//if the fileUID is stored, then the file is locked
		if(lockManager.entryExists(fileUID)){
			return true
		}
		else{
			return false
		}
	}
	
	/**
	 *
	 * Returns the server port number
	 *
	**/
	def getPort: String = {
		return serverSocket.getLocalPort.toString;
	}
	
	
	/**
	 *
	 * Utility function called when a kill service message is received. Shuts down the server
	 *
	**/
	def shutdown(){
		println("Server shutting down...")
		try {
			serverSocket.close()
			pool.shutdownNow()
			System.exit(0)
		}
		catch {
			case e: SocketException => println("SocketException: closing server socket failed")
		}
	}
}
