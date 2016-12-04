import java.io._
import java.net.Socket
import java.nio.file.{Files, Paths}

import Utils._
import sun.misc.IOUtils


object Server {
  val masterKey: Double = 13
  val BUFFER_SIZE       = 8192

  val login             = "test"
  val password          = "zoppico"

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
      def sendEncryptedMessage(message: String): Unit = {
        os.println(encrypt(sessionKey, message))
        os.flush()
      }
      println("connection established")
      os.println("connection established")
      os.flush()

      var requestString: String = null

      do {
        requestString = decrypt(sessionKey, is.readLine())
        println(requestString)
        requestString match {
          case "help" =>
            sendEncryptedMessage(""" "help" - for help, "file" - to send file """)
          case "authorize" =>
            while (true) {
              val clientPass = decrypt(sessionKey, is.readLine())
              if (clientPass == password) {
                sendEncryptedMessage("secret info")
              } else {
                sendEncryptedMessage("Your password or login is not match. Try again! Press any key")
              }
            }
          case "file" =>
            while (true) {
              sendEncryptedMessage("enter login:")
              val clientLogin = decrypt(sessionKey, is.readLine())
              sendEncryptedMessage("password:")
              val clientPass = decrypt(sessionKey, is.readLine())
              if (clientLogin == login && clientPass == password) {
                sendEncryptedMessage(""" ready to save file """)
                saveFile(socket, sessionKey)
              } else {
                sendEncryptedMessage("Your password or login is not match. Try again! Press any key")
                is.readLine()
              }
            }
          case _ =>
            os.println(encrypt(sessionKey, """No such command"""))
            os.flush()
        }

      } while (requestString != null)
    } else {
      os.println("connection refused")
      os.flush()
    }
  }

  def saveFile(socket: Socket, sessionKey: Double): Unit = {
    Files.deleteIfExists(Paths.get("test1.png"))
    Files.deleteIfExists(Paths.get("testdecoded.png"))
    println("removed")
    Files.copy(socket.getInputStream, Paths.get("test1.png"))
    println("saved test1")
    val encodedBytes = Files.readAllBytes(Paths.get("test1.png"))
    val decodedBytes = encodedBytes.map(_ ^ sessionKey.toByte).map(_.toByte)
    Files.write(Paths.get("testdecoded.png"), decodedBytes)
    println("saved")
  }

  def main(args: Array[String]) {
    MyServerSocket(7070, handleConnection)
  }
}
