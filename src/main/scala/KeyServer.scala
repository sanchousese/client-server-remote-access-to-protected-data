import java.io.{BufferedReader, PrintStream}

object KeyServer {
  val handleConnection = (is: BufferedReader, os: PrintStream) => {
    os.println("Hello!")
    os.flush()
    while (true) {
      val task: String = is.readLine()
      println(task)
    }
  }

  def main(args: Array[String]) {
    MyServerSocket(3456, handleConnection)
  }
}
