import java.io.{BufferedReader, InputStreamReader, PrintWriter, BufferedWriter, OutputStreamWriter}
import java.net.Socket

object Client {

	val host = "localhost"
	val port = 7000
	var message: String = ""
	val proxy : ClientProxy = new ClientProxy()
	
	def main(args: Array[String]) {
		Client.run()
	}
	
	def run(){
		proxy.write("../TestFiles/writeTest.txt")
		
		proxy.read("../TestFiles/writeTest.txt")
		
		proxy.modify("../TestFiles/writeTest.txt")
	}
}