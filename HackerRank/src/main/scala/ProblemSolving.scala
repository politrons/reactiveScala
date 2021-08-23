import scala.util.{Failure, Success}

object ProblemSolving {


  def main(args: Array[String]): Unit = {
//    circularArrayRotation
//    stopTryErrorSequence
    reduceCounter
  }

  /**
    * https://www.hackerrank.com/challenges/save-the-prisoner/problem
    *
    * @return
    */
  def saveThePrisoner(): Int = {
    val n = 4 // number of prisoners
    val m = 6 //Number of sweets
    val s = 2 //Chair where we start
    val mod = m % n
    val warnPrisoner = if (mod != 0) {
      mod + s - 1
    } else {
      n
    }
    println(warnPrisoner)
    warnPrisoner
  }

  /**
    * https://www.hackerrank.com/challenges/circular-array-rotation/problem
    */
  def circularArrayRotation(): Array[Int] = {
    val k = 2
    val a = Array(1, 2, 3)
    val query = Array(0, 1, 2)

    var array: Array[Int] = a
    var xx: Array[Int] = Array(a.length)
    for (_ <- 1 to k) {
      val last = array.last
      array.indices.foreach(i => {
        if (i < array.length - 1) {
          array(array.length - 1 - i) = array(array.length - 2 - i)
        } else {
          array(0) = last
        }
      })
    }
    query.indices.foreach(i => {
      query(i) = array(i)
    })
    query
  }

  def stopTryErrorSequence(): Unit ={
    val triedUnit = for {
      _ <- if (true) Failure(new IllegalStateException()) else Success()
      _ <- if (true) Failure(new NullPointerException()) else Success()
      _ <- if (true) {
        Failure(new IllegalAccessException())
      } else{
        Success()
      }
    } yield ()
    println(triedUnit)
  }


  def reduceCounter = {
    for(i <- 2 to 2 by -1) {
      println(i)
    }
  }

}
