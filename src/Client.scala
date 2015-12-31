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
		proxy.write("root/subroot/test.txt")
		proxy.write("root/subroot2/test.txt")
		
		proxy.read("root/subroot2/test.txt")
		proxy.read("root23/test.txt")
	}
}