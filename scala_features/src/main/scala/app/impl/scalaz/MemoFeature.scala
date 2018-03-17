package app.impl.scalaz

import org.junit.Test

import scalaz.Memo


class MemoFeature {

    val slowFib: Int => Int = {
      case 0 => 0
      case 1 => 1
      case n => slowFib(n - 2) + slowFib(n - 1)
    }

  val memoizedFib: Int => Int = Memo.mutableHashMapMemo {
    case 0 => 0
    case 1 => 1
    case n => memoizedFib(n - 2) + memoizedFib(n - 1)
  }

  @Test
  def fibonachi(): Unit = {
    println(slowFib(45))
  }

  @Test
  def fibonachiMemo(): Unit = {
    println(memoizedFib(45))
  }

}
