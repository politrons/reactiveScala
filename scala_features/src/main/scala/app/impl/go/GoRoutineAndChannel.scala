package app.impl.go

import java.util.UUID

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}

object GoRoutineAndChannel {

  case class Channel[T](promise: Promise[T])

  def makeChan[T]: Channel[T] = {
    Channel(Promise[T]())
  }

  //Write response function in channel
  def go[T](func: () => T)(channel: Channel[T]*): Unit = {
    Future {
      if (channel.nonEmpty) channel.head.promise.success(func())
    }
  }

  def goCompose[T, Z](func: Channel[Z] => T)(channel: Channel[T], composeChannel: Channel[Z]): Unit = {
    Future {
      channel.promise.success(func(composeChannel))
    }
  }

  implicit class CustomChannel[T](channel: Channel[T]) {
    def <=(duration: Duration = 100 seconds): T = {
      Await.result(channel.promise.future, duration)
    }
  }

}

import app.impl.go.GoRoutineAndChannel._

class GoRoutineAndChannel {

  @Test
  def asyncStringChannel(): Unit = {
    val channel: Channel[String] = makeChan[String]

    go(() => {
      val newValue = UUID.randomUUID().toString
      s"${Thread.currentThread().getName}-${newValue.toUpperCase}"
    })(channel)

    val responseFromChannel = channel <= ()
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)
  }

  @Test
  def asyncFooChannel(): Unit = {
    val channel: Channel[Foo] = makeChan[Foo]

    go(() => {
      Foo(s"${Thread.currentThread().getName}:I'm Foo type")
    })(channel)

    val responseFromChannel = channel <= ()
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  @Test
  def asyncFireAndForget(): Unit = {
    go(() => {
      println(s"${Thread.currentThread().getName}-Fire & Forget")
    })()
    Thread.sleep(1000)
  }

  /**
    * Using [goCompose] operator we're able to compose multiple [Channels] to get same effect than [flatMap]
    * multiple monads.
    * In this example we create three channels one per type, and we run three process sequentially async each.
    * In every process we get the previous value, and it does not matter if the action to get the value it's blocking
    * since the action also happens asynchronously
    */
  @Test
  def asyncCompositionChannel(): Unit = {
    val channel1: Channel[String] = makeChan[String]
    val channel2: Channel[Foo] = makeChan[Foo]
    val channel3: Channel[Option[Foo]] = makeChan[Option[Foo]]

    go(() => {
      s"${Thread.currentThread().getName}:Hello composition in Scala go"
    })(channel1)

    goCompose[Foo, String](channel => {
      val previousValue = channel <= ()
      Foo(previousValue)
    })(channel2, channel1)

    goCompose[Option[Foo], Foo](channel => {
      val previousValue = channel <= ()
      Some(previousValue)
    })(channel3, channel2)

    val responseFromChannel = channel3 <= ()
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  //We can also specify the time
  @Test
  def asyncChannelWithDuration(): Unit = {
    val channel: Channel[Foo] = makeChan[Foo]

    go(() => {
      Foo(s"${Thread.currentThread().getName}:With timeout")
    })(channel)

    val responseFromChannel = channel <= (10 seconds)
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)
  }

  case class Foo(value: String)

}
