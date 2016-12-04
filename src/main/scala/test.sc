import java.io.PrintStream
import java.nio.file.{Files, Paths}

import sun.misc.IOUtils


val bytes =
  Files.readAllBytes(Paths.get("/Users/Sutula/IdeaProjects/ZI2/src/main/resources/Blank-009267-1024-x-1024-Stripes.png"))

def sendFile(os: PrintStream, key: Double) = {
  val bytes =
    Files.readAllBytes(Paths.get("src/main/resources/Blank-009267-1024-x-1024-Stripes.png"))

  os.write(bytes.map(_ ^ key.toByte).map(_.toByte), 0, bytes.length)
  os.flush()
  println("Sended")
}

