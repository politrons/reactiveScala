package com.politrons.traverse
import cats.implicits._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
object CatsTraverse {

  implicit val ec = scala.concurrent.ExecutionContext.global

  def main(args: Array[String]): Unit = {
    traverseListOption()
    futureTraverse()
  }

  def traverseListOption(): Unit ={
    val list = List(Some(1), Some(2), None)
    val traversed = list.traverse(identity)
    println(traversed)

    val sequence = list.sequence
    println(sequence)

  }

  def futureTraverse(): Unit ={
    val x: Int => Future[String] = x => Future(x.toString)
    val seq: Seq[Int]=Seq(1,2,3,4,5)
    val futureSeq: Future[Seq[String]] = Future.sequence(seq.map(x))
    val ints = Await.result(futureSeq, 10 seconds)
    println(ints)
  }

}
