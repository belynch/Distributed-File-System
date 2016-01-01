import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter}
import java.net.{Socket, ServerSocket, SocketException}
import java.util.concurrent.{Executors}
import scala.io.BufferedSource
import java.io.{File}


trait FileServerInterface {
  def getPort: String
  def writeFile(file: File, UID : Int)
  def fileExists(UID : Int): Boolean
  def getFile(UID: Int): FileEntry
  def shutdown(): Unit
}

/**
 *
 * Object which implements a File Server. A file Server contains a 
 * FileManager used to manage the server's files
 *
**/
object FileServer extends FileServerInterface {

	val pool = java.util.concurrent.Executors.newFixedThreadPool(20)
	var serverSocket: ServerSocket = null
	var port: Int = 0
	
	val fileManager : FileManager = new FileManager()

	/**
	 *
	 * Main function which creates and runs the file server on a given port.
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
	def run(): Unit = {
		try {
			println("Server starting on port: " + port)
			//loop until a worker receives a message to kill the service
			while(!serverSocket.isClosed){
				try {
					pool.execute(new FileServerListener(serverSocket.accept(), this))
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
	
	/**
	 *
	 * Returns the server port number
	 *
	**/
	def getPort: String = {
		return serverSocket.getLocalPort.toString;
	}
	
	def writeFile(file : File, UID : Int){
		println("UID: " + UID)
		fileManager.addEntry(file, UID)	
	}
	
	def fileExists(UID : Int): Boolean = {
		return fileManager.entryExists(UID)
	}
	
	def getFile(UID: Int): FileEntry = {
		return fileManager.getEntry(UID)
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
