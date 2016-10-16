import java.io.{BufferedReader, PrintStream}

object Server {
  def main(args: Array[String]) {
    MyServerSocket(7070, handleConnection)
  }

  val handleConnection = (is: BufferedReader, os: PrintStream) => {
    os.println("Hello!")
    os.flush()
    while (true) {
      val task: String = is.readLine()
      println(task)
    }
  }
}
