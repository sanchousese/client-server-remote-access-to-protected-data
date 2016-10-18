val r = scala.util.Random
println(r.nextInt(100))

import com.roundeights.hasher.Implicits._

val hashMe = "Some String"
val sha1 = hashMe.crc32
sha1.hash.toString()


def encrypt(string: String, step: Int): String = {
  val encoded =
    for {
      s <- string
      newChar = (s.toInt + step).toChar
    } yield newChar

  encoded.mkString
}

def decrypt(string: String, step: Int): String = {
  encrypt(string, -step)
}

val encr = encrypt("Gogi", 10)
val ster = decrypt(encr, 10)

val clientRequest = "fdafds|dfasfdafdasf"

clientRequest.split("\\|").toList match {
  case clientName :: clientNumber :: Nil=>
    println(s"clientName: $clientName -> $clientNumber")
  case _ =>
}
