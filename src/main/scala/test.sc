import java.nio.file.{Files, Paths}

val bytes =
  Files.readAllBytes(Paths.get("/Users/Sutula/IdeaProjects/ZI2/src/main/resources/Blank-009267-1024-x-1024-Stripes.png"))

val r = bytes.map(_ ^ 5)
val r1 = r.map(_ ^ 5)
r1.map(_.toByte).sameElements(bytes)