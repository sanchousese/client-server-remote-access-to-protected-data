import java.io.{BufferedReader, PrintStream}
import java.net.ServerSocket

case class MyServerSocket(port: Int, handleConnection: (BufferedReader, PrintStream) => Unit) {
  val serverSocket = new ServerSocket(port)
  val socket = serverSocket.accept()
  try {
    val (is, os) = Utils.generateStreams(socket)
    handleConnection(is, os)
  } finally {
    socket.close()
  }
}
