import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter}
import java.net.{Socket, ServerSocket, SocketException}
import java.util.concurrent.{Executors}
import scala.io.BufferedSource


trait FileServerInterface {
  def getPort: String
  def shutdown(): Unit
}

/**
 *
 * Object which encapsulates a multithreaded TCP server and worker class.
 *
**/
object FileServer extends FileServerInterface {

	val pool = java.util.concurrent.Executors.newFixedThreadPool(20)
	var serverSocket: ServerSocket = null
	var port: Int = 0
	
	/**
	 *
	 * Main function which creates and runs a server instance on a given port.
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