import java.io.{BufferedReader, PrintStream}
import Utils._

object Server {
  val masterKey: Double = 13

  val handleConnection = (is: BufferedReader, os: PrintStream) => {
    val clientRequest = is.readLine()
    clientRequest.split("\\|").toList match {
      case sessionKey :: encryptedMessage :: Nil =>
        os.println(s"$sessionKey -> ${decrypt(masterKey, encryptedMessage)}")
        os.flush()
      case _ =>
        os.println("Match error, please send message with format: [client_name]|[target_name]|[random_number]")
        os.flush()
    }
  }

  def main(args: Array[String]) {
    MyServerSocket(7070, handleConnection)
  }
}
