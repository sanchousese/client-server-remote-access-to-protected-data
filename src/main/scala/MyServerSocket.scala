import java.io.{BufferedReader, PrintStream}
import java.net.{ServerSocket, Socket}
import scala.concurrent._
import ExecutionContext.Implicits.global

case class MyServerSocket(port: Int, handleConnection: (Socket, BufferedReader, PrintStream) => Any) {
  val serverSocket = new ServerSocket(port)
  while (true) {
    val socket = serverSocket.accept()
    Future {
      try {
        val (is, os) = Utils.generateStreams(socket)
        handleConnection(socket, is, os)
      } finally {
        socket.close()
      }
    }
  }
}
