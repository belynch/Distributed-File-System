import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter}
import java.net.{Socket, ServerSocket, SocketException}
import java.util.concurrent.{Executors}
import scala.io.BufferedSource

//The directory service should provide an administrative interface as detailed in the annex. 
//This will be used by the test system to build global file system metadata indicating a set 
//of files with specific names located on specific file servers.
trait DirectoryServerInterface {
  def getPort: String
  def writeFile(file : String): Boolean
  def fileExists(file : String): Boolean
  def getCurrentUID(): Int
  def getFileUID(file : String): Int
  def getFileState(file : String): Int
  def updateFileState(file : String)
  def getFileServerIP: String
  def getFileServerPort: Int

  def shutdown(): Unit
}

/**
 *
 * Object which implements a Directory Server. A directory Server contains a 
 * DreictoryManager used in the management of the directory space
 *
**/
object DirectoryServer extends DirectoryServerInterface {

	val pool = java.util.concurrent.Executors.newFixedThreadPool(20)
	var serverSocket: ServerSocket = null
	var port: Int = 0
	val directory : DirectoryManager =  new DirectoryManager()
	
	val fileServerIP : String = "localhost"
	val fileServerPort : Int = 8000
	
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
					pool.execute(new DirectoryServerListener(serverSocket.accept(), this))
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
	
	/**
	 *
	 * Adds a new file entry to the servers directory manager if the file path
	 * doesn't already exist
	 *
	**/
	def writeFile(file : String): Boolean = {
		
		var writeSuccess : Boolean = false;
		//extract file name and parent directory from path
		var index: Int = file.lastIndexOf('/')
		var split = file.splitAt(index + 1)
		val parent: String = split._1
		val name: String = split._2
		
		//if the file doesn't exist, add it
		if(!fileExists(file)) {
			writeSuccess = true
			directory.addEntry(parent, name, fileServerIP, fileServerPort)
		}
			
		return writeSuccess
	}
	
	def fileExists(file : String): Boolean = {
		return directory.entryExists(file)
	}
	
	def getCurrentUID(): Int = {
		return directory.getId()
	}
	
	def getFileUID(file : String): Int = {
		var UID : Int = -1
		if(fileExists(file)){
			UID = directory.getUID(file)
		}
		return UID
	}
	
	def getFileState(file : String): Int = {
		return directory.getEntry(file).state
	}
	
	def updateFileState(file : String){
		directory.getEntry(file).updateState()
	}

	def getFileServerIP: String = {
		return fileServerIP
	}
	
	def getFileServerPort: Int = {
		return fileServerPort
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
