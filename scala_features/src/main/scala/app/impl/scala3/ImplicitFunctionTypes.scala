package app.impl.scala3

import org.junit.Test

import scala.collection.mutable.ListBuffer

/**
  * This example still cannot use [implicit function types] since it will come with dotty(Scala3).
  * Once we can we will be able to declare an implicit function type alias as Transactional[T]
  * and use it in our return type instead of [Transaction => String]
  * Then thanks to define [thisTransaction] which use [implicitly] it wont be necessary to
  * have to use the [implicit thisTransaction: Transaction] in every function.
  */
class ImplicitFunctionTypes {


  //  type Transactional[T] = implicit Transaction => T
  //  def thisTransaction: Transactional[Transaction] = implicitly[Transaction]


  @Test
  def mainTransaction(): Unit = {
    runTransaction(transactionFunc)
  }

  /**
    * Here we receive a function with all computation to do against a transaction, so we create the empty
    * transaction and we pass to the composed functions.
    * Once the transaction function has finish we invoke commit of the transaction to finish.
    */
  def runTransaction[T](transFunc: Transaction => T) = {
    val trans: Transaction = new Transaction
    transFunc(trans)
    trans.commit()
  }

  /**
    * In this function we declare that whatever we pass as Transaction to this function it must be declared
    * by the compiler as implicit in the context of the application.
    */
  private def transactionFunc: Transaction => Unit = {
    implicit thisTransaction =>
      val res = transaction1("hello")
        .apply(thisTransaction)
        .map(value => value.toUpper)
      println(s"result: $res")
  }

  /**
    * Curried function which receive as implicit the state of the transaction, then we do some operation
    * over the transaction, and we invoke the transaction 2.
    *
    * @param sentence value to apply over the transaction
    * @return the Function Transaction => String
    */
  def transaction1(sentence: String): Transaction => String = {
    implicit thisTransaction: Transaction =>
      thisTransaction.println(s"First transaction: $sentence")
      transaction2(sentence ++ " implicit").apply(thisTransaction)
  }

  /**
    * Curried function which receive as implicit the state of the transaction, then we do some operation
    * over the transaction, and we invoke the transaction 2.
    *
    * @param sentence value to apply over the transaction
    * @return the Function Transaction => String
    */
  def transaction2(sentence: String): Transaction => String = {
    implicit thisTransaction: Transaction =>
      thisTransaction.println(s"Second transaction: $sentence")
      transaction3(sentence ++ " functions").apply(thisTransaction)
  }

  /**
    * Curried function which receive as implicit the state of the transaction, then we do some operation
    * over the transaction, and we then return the last operation value.
    *
    * @param sentence value to apply over the transaction
    * @return the sentence String
    */
  def transaction3(sentence: String): Transaction => String = {
    implicit thisTransaction: Transaction =>
      thisTransaction.println(s"Third transaction: $sentence")
      sentence
  }


  class Transaction {
    private val log = new ListBuffer[String]

    def println(s: String): Unit = log += s

    def commit(): Unit = {
      Console.println("******* Transactions ********")
      log.foreach(Console.println)
    }
  }

}


