import java.io.{BufferedReader, PrintStream}
import java.net.{ServerSocket, Socket}

case class MyServerSocket(port: Int, handleConnection: (Socket, BufferedReader, PrintStream) => AnyVal) {
  val serverSocket = new ServerSocket(port)
  val socket = serverSocket.accept()
  try {
    val (is, os) = Utils.generateStreams(socket)
    handleConnection(socket, is, os)
  } finally {
    socket.close()
  }
}
