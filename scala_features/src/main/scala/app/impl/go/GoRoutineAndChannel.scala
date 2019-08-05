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

  //Fire & Forget
  def go[T](func: () => T): Unit = {
    Future {
      func()
    }
  }

  //Write response function in channel
  def go[T](channel: Channel[T], func: () => T): Unit = {
    Future {
      channel.promise.success(func())
    }
  }

  def goCompose[T, Z](channel: Channel[T], composeChannel: Channel[Z], func: Channel[Z] => T): Unit = {
    Future {
      channel.promise.success(func(composeChannel))
    }
  }

  implicit class CustomChannel[T](channel: Channel[T]) {
    def <=(): T = {
      Await.result(channel.promise.future, 100 seconds)
    }

    def <=(duration: Duration): T = {
      Await.result(channel.promise.future, duration)
    }
  }

}

import app.impl.go.GoRoutineAndChannel._

class GoRoutineAndChannel {

  @Test
  def asyncStringChannel(): Unit = {
    val channel: Channel[String] = makeChan[String]

    go(channel, () => {
      val newValue = UUID.randomUUID().toString
      s"${Thread.currentThread().getName}-${newValue.toUpperCase}"
    })

    val responseFromChannel = channel <= ()
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  @Test
  def asyncFooChannel(): Unit = {
    val channel: Channel[Foo] = makeChan[Foo]

    go(channel, () => {
      Foo(s"${Thread.currentThread().getName}:I'm Foo type")
    })

    val responseFromChannel = channel <= ()
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  @Test
  def asyncFireAndForget(): Unit = {
    go(() => {
      println(s"${Thread.currentThread().getName}-Fire & Forget")
    })

    Thread.sleep(1000)

  }

  @Test
  def asyncCompositionChannel(): Unit = {
    val channel1: Channel[String] = makeChan[String]
    val channel2: Channel[Foo] = makeChan[Foo]


    go(channel1, () => {
      s"${Thread.currentThread().getName}:Hello composition in Scala go"
    })

    goCompose[Foo, String](channel2, channel1, channel => {
      val previousValue = channel <= ()
      Foo(previousValue)
    })

    val responseFromChannel = channel2 <= ()
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  //We can also specify the time
  @Test
  def asyncChannelWithDuration(): Unit = {
    val channel: Channel[Foo] = makeChan[Foo]

    go(channel, () => {
      Foo(s"${Thread.currentThread().getName}:With timeout")
    })

    val responseFromChannel = channel <= (10 seconds)
    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  case class Foo(value: String)

}
