import java.net.Socket

object Client {
  def main(args: Array[String]) {
    val keyServerSocket = new Socket("localhost", 3456)
    try {
      val (is, os) = Utils.generateStreams(keyServerSocket)
      while (true) {
        if(is.ready()) {
          os.println(is.readLine())
          Thread.sleep(3000)
        }
      }
    } finally {
      keyServerSocket.close()
    }
  }
}
