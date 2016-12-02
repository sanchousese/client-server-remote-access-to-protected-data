import java.io.{BufferedReader, PrintStream}
import java.net.Socket
import java.nio.file.{Files, Paths}

import Utils._
import sun.misc.IOUtils

object Server {
  val masterKey: Double = 13

  val handleConnection = (socket: Socket, is: BufferedReader, os: PrintStream) => {
    val clientRequest = is.readLine()
    println(clientRequest)
    val randValue = generateRandomKey
    val sessionKey: Double =
      clientRequest.split("\\|").toList match {
        case sesKey :: encryptedMessage :: Nil =>
          println(encrypt(sesKey.toDouble, s"$randValue|B"))
          os.println(encrypt(sesKey.toDouble, s"$randValue|B"))
          os.flush()
          sesKey.toDouble
        case _ =>
          os.println("Match error, please send message with format: [client_name]|[target_name]|[random_number]")
          os.flush()
          -1
      }

    val secondRequest = is.readLine()
    println(secondRequest)
    val randomValueWithFunc = decrypt(sessionKey, secondRequest).toDouble
    if (randomValueWithFunc == randValue + 100) {
      println("connection established")
      os.println("connection established")
      os.flush()

      var requestString: String = null
      do {
        requestString = is.readLine()
        println(requestString)
        requestString match {
          case "help" =>
            os.println(""" "help" - for help, "file" - to send file """)
            os.flush()
          case "file" =>
            os.println(""" ready to save file """)
            os.flush()
            Files.deleteIfExists(Paths.get("test1.png"))
            Files.deleteIfExists(Paths.get("testdecoded.png"))
            Files.copy(socket.getInputStream, Paths.get("test1.png"))
            val encodedBytes = Files.readAllBytes(Paths.get("test1.png"))
            val decodedBytes = encodedBytes.map(_ ^ sessionKey.toByte).map(_.toByte)
            Files.write(Paths.get("testdecoded.png"), decodedBytes)
        }

      } while (requestString != null && requestString != "exit")
    } else {
      os.println("connection refused")
      os.flush()
    }
  }

  def main(args: Array[String]) {
    MyServerSocket(7070, handleConnection)
  }
}
