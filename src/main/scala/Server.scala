import java.io.{BufferedReader, PrintStream}
import java.net.Socket
import java.nio.file.{Files, Paths}

import Utils._
import sun.misc.IOUtils

object Server {
  val masterKey: Double = 13

  val handleConnection = (socket: Socket, is: BufferedReader, os: PrintStream) => {
    val clientRequest = is.readLine()
    val randValue = generateRandomKey
    val sessionKey: Double =
      clientRequest.split("\\|").toList match {
        case sesKey :: encryptedMessage :: Nil =>

          os.println(encrypt(sesKey.toDouble, s"$randValue|B"))
          os.flush()
          sesKey.toDouble
        case _ =>
          os.println("Match error, please send message with format: [client_name]|[target_name]|[random_number]")
          os.flush()
          -1
      }

    val secondRequest = is.readLine()
    val randomValueWithFunc = decrypt(sessionKey, secondRequest).toDouble
    if (randomValueWithFunc == randValue + 100) {
      os.println("connection established")
      os.flush()
//      IOUtils.readFully()
      Files.copy(socket.getInputStream, Paths.get("test1.png"))
      val encodedBytes = Files.readAllBytes(Paths.get("test1.png"))
      val decodedBytes = encodedBytes.map(_ ^ sessionKey.toByte).map(_.toByte)
      Files.write(Paths.get("testdecoded.png"), decodedBytes)
    } else {
      os.println("connection refused")
      os.flush()
    }
  }

  def main(args: Array[String]) {
    MyServerSocket(7070, handleConnection)
  }
}
