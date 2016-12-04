import java.io.File

import scala.io.Source

for(line <- Source.fromFile(new File("/Users/Sutula/IdeaProjects/ZI2/cain.txt")).getLines())
  println(line)