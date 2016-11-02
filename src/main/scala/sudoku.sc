import scala.collection.immutable.IndexedSeq
import scala.collection.mutable
import scala.io.Source

val sudoku = readSudoku("/Users/Sutula/IdeaProjects/ZI2/src/main/resources/sudoku.txt")

solveSudoku(sudoku).foreach(a => println(a.mkString(", ")))

def readSudoku(fileName: String): Array[Array[Int]] = {
  Source.fromFile(fileName).getLines()
    .map(s => {
      s.split(" ").map(c => if (c == "_") -1 else c.toInt)
    }).toArray
}

def rejectByHorizontal(sudoku: Array[Array[Int]],
                       rowNumber: Int): Array[Int] = {
  sudoku(rowNumber).filter(_ != -1)
}

def rejectByVertical(sudoku: Array[Array[Int]],
                     colNumber: Int): Array[Int] = {
  sudoku.map(_ (colNumber)).filter(_ != -1)
}

def rejectBySquare(sudoku: Array[Array[Int]], row: Int, col: Int): Array[Int] = {
  val (rRow, rCol) = (row / 3, col / 3)
  val arr =
    for {
      x <- 3 * rCol to 3 * rCol + 2
      y <- 3 * rRow to 3 * rRow + 2
    } yield sudoku(x)(y)
  arr.toArray.filter(_ != -1)
}

def sudokuHasError(sudoku: Array[Array[Int]]): Boolean = {
  def checkHorizontal: Boolean = {
    sudoku.exists(a => {
      val ap = a.filter(_ > 0)
      ap.diff(ap.distinct).nonEmpty
    })
  }

  def checkVertical: Boolean = {
    (0 to 8).exists(i => {
      val a = sudoku.map(_ (i)).filter(_ > 0)
      a.diff(a.distinct).nonEmpty
    })
  }

  def checkSquares: Boolean = {
    def chk(squares: IndexedSeq[(Int, Int)]): Boolean = {
      if (squares.length < 9) {
        false
      } else {
        val arr = squares.take(9).map {
          case (x, y) => sudoku(y)(x)
        }.filter(_ > 0)
        if (arr.diff(arr.distinct).nonEmpty) {
          true
        } else {
          chk(squares.drop(9))
        }
      }
    }

    val squaresElem =
      for {
        i <- 0 to 2
        j <- 0 to 2
        x <- 3 * i to 3 * i + 2
        y <- 3 * j to 3 * j + 2
      } yield (x, y)

    chk(squaresElem)
  }

  checkHorizontal || checkVertical || checkSquares
}

def hasUndefinedNumbers(prevSudoku: Array[Array[Int]]): Boolean = {
  prevSudoku.exists(_.exists(_ == -1))
}

def getUndefinedNumbers(prevSudoku: Array[Array[Int]]): IndexedSeq[(Int, Int)] = {
  val undefinedNumbers =
    for {
      x <- 0 to 8
      y <- 0 to 8
      if prevSudoku(y)(x) == -1
    } yield (x, y)
  undefinedNumbers
}

def guess(prevSudoku: Array[Array[Int]]): Array[Array[Int]] = {
  val undefinedNumbers: IndexedSeq[(Int, Int)] = getUndefinedNumbers(prevSudoku)
  val sudoku = prevSudoku.map(_.clone())
  var result: Array[Array[Int]] = Array(Array())

  undefinedNumbers.foreach {
    case (x, y) =>
      val rejectedEls = mutable.Set() ++
        rejectByVertical(sudoku, x) ++
        rejectByHorizontal(sudoku, y) ++
        rejectBySquare(sudoku, x, y)
      val maybeValues = (1 to 9).filter(!rejectedEls.contains(_))
      if (maybeValues.length == 2) {
        val sudoku1 = sudoku.map(_.clone())
        sudoku1(y).update(x, maybeValues.head)
        val res1 = solveSudoku(sudoku1)
        result =
          if (hasUndefinedNumbers(res1)) {
            val sudoku2 = sudoku.map(_.clone())
            sudoku2(y).update(x, maybeValues.tail.head)
            solveSudoku(sudoku2)
          } else {
            res1
          }

      }
  }
  result
}

def solveSudoku(prevSudoku: Array[Array[Int]]): Array[Array[Int]] = {
  val undefinedNumbers: IndexedSeq[(Int, Int)] = getUndefinedNumbers(prevSudoku)

  if (sudokuHasError(prevSudoku) || undefinedNumbers.isEmpty) {
    prevSudoku
  } else {
    val sudoku = prevSudoku.map(_.clone())
    var changed = false
    undefinedNumbers.foreach {
      case (x, y) =>
        val rejectedEls = mutable.Set() ++
          rejectByVertical(sudoku, x) ++
          rejectByHorizontal(sudoku, y) ++
          rejectBySquare(sudoku, x, y)
        val maybeValues = (1 to 9).filter(!rejectedEls.contains(_))
        if (maybeValues.isEmpty) {
          println("Error")
          return prevSudoku
        }
        if (maybeValues.length == 1) {
          sudoku(y).update(x, maybeValues.head)
          changed = true
        }
    }
    if (!changed) {
      guess(sudoku)
    } else {
      solveSudoku(sudoku)
    }
  }
}




