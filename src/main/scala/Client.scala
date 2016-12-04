import java.io.{File, PrintStream}
import java.net.Socket
import java.nio.file.{Files, Paths}

import scala.util.control.Breaks._
import Utils._

import scala.io.Source

object Client {
  val masterKey: Double = 15

  def sendFile(os: PrintStream, key: Double) = {
    val bytes =
      Files.readAllBytes(Paths.get("src/main/resources/Blank-009267-1024-x-1024-Stripes.png"))

    os.write(bytes.map(_ ^ key.toByte).map(_.toByte), 0, bytes.length)
    os.flush()
    println("Sended")
  }

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

      println("requestToB: " + requestToB)
      val sessionKey = requestToB.split("\\|").head.toDouble

      val (isS, osS) = generateStreams(targetServerSocket)
      osS.println(requestToB)
      osS.flush()

      val targetResponse = isS.readLine()
      println(decrypt(sessionKey, targetResponse))
      decrypt(sessionKey, targetResponse).split("\\|").toList match {
        case randomTargetNumber :: targetName :: Nil if targetName == "B" =>
          osS.println(encrypt(sessionKey, (randomTargetNumber.toDouble + 100).toString))
          osS.flush()
        case _ =>
          osS.println("Error! Something goes wrong!")
          osS.flush()
      }

      val targetConnectionStatus = isS.readLine()
      println(targetConnectionStatus)
      if (targetConnectionStatus == "connection established") {
        osS.println(encrypt(sessionKey, "authorize"))
        osS.flush()

        for(line <- Source.fromFile(new File("cain.txt")).getLines()) {
          val request = line
          osS.println(encrypt(sessionKey, request))
          osS.flush()
          val response = decrypt(sessionKey, isS.readLine())
          if (response == "secret info") {
            println(s"$line -> $response")
          }
        }
//        while (true) {
//          val request = scala.io.StdIn.readLine()
//          osS.println(encrypt(sessionKey, request))
//          osS.flush()
//          val response = decrypt(sessionKey, isS.readLine())
//          if (response == "secret info") {
//            println(response)
//          }
//        }
//        while (true) {
//          val request = scala.io.StdIn.readLine()
//          osS.println(encrypt(sessionKey, request))
//          osS.flush()
//          val response = decrypt(sessionKey, isS.readLine())
//          println(response)
//          if (response == """ ready to save file """) {
//            sendFile(osS, sessionKey)
//            println(decrypt(sessionKey, isS.readLine()))
//          }
//        }
      }

    } finally {
      keyServerSocket.close()
      targetServerSocket.close()
    }
  }
}
