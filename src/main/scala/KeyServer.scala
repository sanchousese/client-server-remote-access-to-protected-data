import java.io.{BufferedReader, PrintStream}

object KeyServer {
  val secret: Double = 3

  val handleConnection = (is: BufferedReader, os: PrintStream) => {
    val sessionKey = Utils.handShake(secret, is, os)
    println(sessionKey)
  }

  def main(args: Array[String]) {
    MyServerSocket(3456, handleConnection)
  }
}
