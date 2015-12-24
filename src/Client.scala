import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter}
import java.net.Socket

object Client {

	val host = "localhost"
	val port = 8000
	var message: String = ""
	
	def main(args: Array[String]) {
		Client.run()
	}
	
	def run(){
		try{
			val socket = new Socket(host, port)	
			var sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
			val sOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")))
		
			///////////////////TEST1/////////////////////
			sOut.println("HELO")
			sOut.flush()
			println("sent HELO")
			
			message = sIn.readLine()
			println("Received message: " + message)
			
			///////////////////TEST2/////////////////////
			//sOut.println("KILL_SERVICE")
			//sOut.flush()
			//println("sent KILL_SERVICE")
			
			///////////////////TEST3/////////////////////
			sOut.println("DISCONNECT")
			sOut.flush()
			println("sent DISCONNECT")
			
			message = sIn.readLine()
			println("Received message: " + message)
			message = sIn.readLine()
			println("Received message: " + message)
			message = sIn.readLine()
			println("Received message: " + message)
			
			///////////////////TEST4/////////////////////
			//READ TEST
			//query directory server
		   
			
			//receive file location
			
			
			//request file
			
			
			//open file
			
			
			
			///////////////////TEST5/////////////////////
			//WRITE TEST
			//query directory server
		   
			
			//receive file location
			
			
			//request file
			
			
			//modify file
			
			
			//upload file
		
			socket.close()
		}
		catch{
			case notConnected : java.net.ConnectException => println("Can't connect to: " + host + " on port: "+ port)
		}
	}
}