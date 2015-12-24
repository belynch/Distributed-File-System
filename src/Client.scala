import java.io._
import java.net.Socket

object Client {

  val host = "localhost"
  val port = 8000

  def run(){
    try{
        val socket = new Socket(host, port)	
		var sIn = new BufferedReader(new InputStreamReader(socket.getInputStream))
		val sOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")))
		
		///////////////////TEST1/////////////////////
		//query directory server
        println("Reading")
        sOut.println("FREAD:test.txt")
        sOut.flush() 
        println("Received")
		
		//receive file location
		
		
		//request file
		
		
		//open file
		
		
		
		///////////////////TEST2/////////////////////
		println("Writing")
        sOut.println("FWRITE:test.txt")
        sOut.println("CONTENTS:test")
        sOut.flush() 
        println("Received")
    
        socket.close()
    }
	catch
	{
      case notConnected : java.net.ConnectException => println("Can't connect to: " + host + " on port: "+ port)
    }
  }

  def main(args: Array[String]) {
     Client.run()
  }
}