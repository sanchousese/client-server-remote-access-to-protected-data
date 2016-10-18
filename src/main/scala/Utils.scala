import java.io.{BufferedOutputStream, BufferedReader, InputStreamReader, PrintStream}
import java.net.Socket

object Utils {
  val r = scala.util.Random

  val p = 61
  val g = 53

  def generateStreams(socket: Socket): (BufferedReader, PrintStream) = {
    val inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val outputStream = new PrintStream(new BufferedOutputStream(socket.getOutputStream))
    (inputStream, outputStream)
  }

  def generatePublicKey(secret: Double, base: Double = g): Double = {
    Math.pow(base, secret) % p
  }

  def generateRandomKey: Double = {
    r.nextInt(100)
  }

  def encrypt(step: Double, string: String): String = {
    val encoded =
      for {
        s <- string
        newChar = (s.toInt + step).toChar
      } yield newChar

    encoded.mkString
  }

  def decrypt(step: Double, string: String): String = {
    encrypt(-step, string)
  }
}
