import java.net.Socket
import Utils._

object Client {
  val masterKey: Double = 15

  def main(args: Array[String]) {
    val keyServerSocket = new Socket("localhost", 3456)
    val targetServerSocket = new Socket("localhost", 7070)
    try {
      val (isK, osK) = generateStreams(keyServerSocket)

      val randomNumber = generateRandomKey
      val request: String = s"A|B|$randomNumber"
      osK.println(request)
      osK.flush()

      val kdcResponse = isK.readLine()
      val requestToB =
        decrypt(masterKey, kdcResponse)
          .replace(s"|$request", "")
          .replace(s"|$randomNumber", "")

      val (isS, osS) = generateStreams(targetServerSocket)
      osS.println(requestToB)
      osS.flush()

      val serverResponse = isS.readLine()
      println(serverResponse)

    } finally {
      keyServerSocket.close()
      targetServerSocket.close()
    }
  }
}
