import java.io.{BufferedOutputStream, BufferedReader, InputStreamReader, PrintStream}
import java.net.Socket

object Utils {
  def generateStreams(socket: Socket): (BufferedReader, PrintStream) = {
    val inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val outputStream = new PrintStream(new BufferedOutputStream(socket.getOutputStream))
    (inputStream, outputStream)
  }
}
