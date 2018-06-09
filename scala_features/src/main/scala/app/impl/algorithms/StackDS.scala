package app.impl.algorithms

import org.junit.Test

import scala.collection.mutable

class StackDS {

  @Test
  def mainStack(): Unit = {
    //    main(Array("1 83", "3", "2", "1 76"))
    main(Array("1 97", "2", "1 20", "2", "1 26", "1 20", "2", "3", "1 91", "3"))
  }

  //  1 x  -Push the element x into the stack.
  //  2    -Delete the element present at the top of the stack.
  //  3    -Print the maximum element in the stack.
  def main(args: Array[String]) {
    val stack = mutable.Stack[String]()
    args foreach (element => {
      val command = element.split(" ")(0)
      command match {
        case "1" => stack.push(element.split(" ")(1))
        case "2" => stack.pop()
        case "3" => println(stack.max + " ")
      }
    })

  }

  @Test
  def minimumStack(): Unit = {
    minimumStack(Array(10, 20, 8, 3, 7, 9, 6))
  }

  def minimumStack(args: Array[Int]) {
    val stack = mutable.Stack[Int]()
    val supportStack = mutable.Stack[Int]()
    supportStack.push(args.head)
    args.foreach(value => {
      stack.push(value)
      if (supportStack.top > value) {
        supportStack.push(value)
      }
    })
    print(supportStack.top)
  }

}
