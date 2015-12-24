import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter}
import java.net.{Socket, ServerSocket, SocketException}
import scala.io.BufferedSource

/**
 *
 * Worker thread class
 *
**/
class DirectoryServerListener(socket:Socket, serverInterface:DirectoryServerInterface) extends Runnable {

	var sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
	val sOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")))
	var message = ""
	val IPaddress = socket.getLocalAddress().toString().drop(1)

	/**
	 * Worker run function
	 *
	 * Receives messages over a given socket and handles responses.
	**/
	def run(){
		println("THREAD " + Thread.currentThread().getId()+": running")
		try {
			while(!socket.isClosed()){
				if(socket.getInputStream().available() > 0){
					message = ""
					message = sIn.readLine()
					println("Received message: " + message)
					//BASE 
					if(message.startsWith("HELO")){
						base(message)
					}
					//TERMINATE CLIENT CONNECTION
					else if(message.startsWith("DISCONNECT")){
						disconnect()
					}
					//KILL
					else if(message == "KILL_SERVICE"){
						killService()
					}	
					//UNHANDLED			
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
	}
	
	def base(message:String){
		sOut.println(message + "\nIP:" + IPaddress 
								+"\nPort:" + serverInterface.getPort)
		sOut.flush()
	}
	
	def disconnect() {
		message = sIn.readLine()
		
		sOut.close
		sIn.close
		//tempClient.socket.close
	}
	
	def killService(){
		serverInterface.shutdown()
		socket.close()
	}
	
}