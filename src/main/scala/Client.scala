import java.net.Socket

object Client {
  val secret: Double = 5

  def main(args: Array[String]) {
    val keyServerSocket = new Socket("localhost", 3456)
    try {
      val (is, os) = Utils.generateStreams(keyServerSocket)

      val sessionKey = Utils.handShake(secret, is, os)
      println(sessionKey)
    } finally {
      keyServerSocket.close()
    }
  }
}
