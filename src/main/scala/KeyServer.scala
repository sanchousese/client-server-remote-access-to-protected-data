import java.io.{BufferedReader, PrintStream}

object KeyServer {
  val secret: Double = 3

  val handleConnection = (is: BufferedReader, os: PrintStream) => {
    val BPublic = is.readLine().toDouble
    println(BPublic)

    val APublic = Utils.generatePublicKey(secret)
    os.println(APublic)
    os.flush()

    val sessionKey = Utils.generatePublicKey(secret, BPublic)
    println(sessionKey)
  }

  def main(args: Array[String]) {
    MyServerSocket(3456, handleConnection)
  }
}
