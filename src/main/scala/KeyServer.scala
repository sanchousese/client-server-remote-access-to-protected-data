import java.io.{BufferedReader, PrintStream}
import java.net.Socket

import Utils._

object KeyServer {
  val partsKeys = Map("A" -> 15, "B" -> 13)

  val handleConnection = (socket: Socket, is: BufferedReader, os: PrintStream) => {
    val clientRequest = is.readLine()
    clientRequest.split("\\|").toList match {
      case clientName :: targetName :: clientNumber :: Nil =>
        println(s"clientName: $clientName -> $targetName -> $clientNumber")
        if (!partsKeys.contains(clientName) || !partsKeys.contains(targetName)) {
          os.println("There is no client or target with such name")
          os.flush()
        } else {
          val sessionKey = generateRandomKey
          val response = encrypt(partsKeys(clientName), s"$sessionKey" +
            s"|$clientRequest" +
            s"|$clientNumber" +
            s"|${encrypt(partsKeys(targetName), s"$sessionKey|$clientName")}")
          os.println(response)
          os.flush()
        }
      case _ =>
        os.println("Match error, please send message with format: [client_name]|[target_name]|[random_number]")
        os.flush()
    }
  }

  def main(args: Array[String]) {
    MyServerSocket(3456, handleConnection)
  }
}
