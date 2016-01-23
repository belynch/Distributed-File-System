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
	
	/**
	 * Available commands:
	 *		read(filePath)
	 *		write(filePath)
	 * 		lock(filePath)
	 *		unlock(filePath)
	 *
	 *		where 'filePath' is the path to the file on the directory
	 *		server
	**/
	def run(){
		//test write command - success
		proxy.write("../TestFiles/writeTest.txt")
		
		//test read command - success
		proxy.read("../TestFiles/writeTest.txt")
		
		//test if it is read from cache - success
		proxy.read("../TestFiles/writeTest.txt")
		
		//test if its state on directory server is updated - success
		proxy.write("../TestFiles/writeTest.txt")
		
		//test if cache entry is updated - success
		proxy.read("../TestFiles/writeTest.txt")
	}
}