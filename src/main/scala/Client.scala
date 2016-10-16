import java.net.Socket

object Client {
  val secret: Double = 5

  def main(args: Array[String]) {
    val keyServerSocket = new Socket("localhost", 3456)
    try {
      val (is, os) = Utils.generateStreams(keyServerSocket)
      val APublic = Utils.generatePublicKey(secret)
      os.println(APublic)
      os.flush()

      val BPublic = is.readLine().toDouble
      println(BPublic)

      val sessionKey = Utils.generatePublicKey(secret, BPublic)
      println(sessionKey)
    } finally {
      keyServerSocket.close()
    }
  }
}
