import java.io.{BufferedOutputStream, BufferedReader, InputStreamReader, PrintStream}
import java.net.Socket

object Utils {
  val p = 17
  val g = 3

  def generateStreams(socket: Socket): (BufferedReader, PrintStream) = {
    val inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val outputStream = new PrintStream(new BufferedOutputStream(socket.getOutputStream))
    (inputStream, outputStream)
  }

  def generatePublicKey(secret: Double, base: Double = g): Double = {
    Math.pow(base, secret) % p
  }

  def handShake(secret: Double, is: BufferedReader, os: PrintStream): Double = {
    val APublic = generatePublicKey(secret)
    os.println(APublic)
    os.flush()

    val BPublic = is.readLine().toDouble
    println(BPublic)

    val sessionKey = generatePublicKey(secret, BPublic)
    sessionKey
  }
}
