package app.impl.scala

import org.junit.Test

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class FutureSequence {

  case class Account(status: String)

  var futureList: Future[List[Account]] = Future {
    List(Account("test"),Account("future"),Account("sequence"))
  }

  @Test
  def main(): Unit = {
    val eventualEventualAccounts = futureList.map(list => {
      list.map(account => {
        transform(account.status).map(value => Account(value))
      })
    }).flatMap(futureOfList => {
      Future.sequence(futureOfList)
    })
    eventualEventualAccounts.foreach(account => println(account))
  }

  def transform(s: String): Future[String] = {
    Future {
      s.toUpperCase
    }
  }
}