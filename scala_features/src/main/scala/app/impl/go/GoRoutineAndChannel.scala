package app.impl.go

import java.util.UUID

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success, Try}


//***************************//
//       DSL OF LIBRARY      //
//***************************//

/**
  * This implementation try to emulate how asynchronous programing works in Golang using
  * [Go routines] and [channels].
  *
  * The library use the next component described below.
  */
object GoRoutineAndChannel {

  /**
    * Channel: ADT(Algebra data type) which contains as constructor a Promise of T defined in the Channel.
    */
  case class Channel[T](var promise: Promise[T])

  case class NoMoreElements()

  case class Panic() extends Exception

  /**
    * makeChan[T]: Factory method  which create an instance of [Channel using the type T]
    */
  def makeChan[T]: Channel[T] = {
    Channel(Promise[T]())
  }

  /**
    *
    * go: Operator make the function that we pass run asynchronously, and with the response of that function, in case
    * we pass a Channel it will write the response in there.
    *
    * As the second argument for channel we use varargs so in case we dont provide a channel, we will make fire & forget
    *
    * @param func     supplier function that it will be executed asynchronously.
    * @param channels where it will write the response of the function
    * @tparam T Type of the input and output type of the channel
    **/
  def go[T](func: () => T)(channels: Channel[T]*): Unit = {
    Future {
      if (channels.nonEmpty) {
        channels.head.promise.success(func())
      }
    }
  }

  /**
    * goCompose: Operator similar to [flatMap] to make the function that we pass run asynchronously and allow composition.
    *
    * @param func           with input [composeChannel] where we will compose a previous channel response with the logic of the new one.
    * @param channel        where it will write the response of the function
    * @param composeChannel previously used in another [go] or [goCompose] operator, and contains some response value to compose.
    * @tparam Z Type of the input and output type of the channel
    * @tparam T Type of the compose channel
    */
  def goCompose[T, Z](func: Channel[Z] => T)(channel: Channel[T], composeChannel: Channel[Z]): Unit = {
    Future {
      channel.promise.success(func(composeChannel))
    }
  }

  /**
    * Operator to read from the channel the response of the asynchronous function.
    * Just like in Golang the operator it's blocking until get the response, or the timeout it's reached.
    *
    * Once we're able to get the response from the future we will refresh the promise from the channel
    *
    * Just to make it looks like Golang, we return a tuple of possible value to the left, and the error to the right.
    * This is something that definitely Scala do better with Monad Either where the Right is always the right effect value.
    */
  def <=[T](channel: Channel[T], duration: Duration = 1 seconds): (T, NoMoreElements) = {
    Try(Await.result(channel.promise.future, duration)) match {
      case Success(response) =>
        channel.promise = Promise()
        (response, null)
      case Failure(_) => (null.asInstanceOf[T], NoMoreElements())
    }
  }

}

import app.impl.go.GoRoutineAndChannel._

//***************************//
//    EXAMPLE USE OF DSL     //
//***************************//
class GoRoutineAndChannel {

  /**
    * In this example we just create the channel using [makeChan] and we pass together with a function
    * to the operator [go] which it will make the function run asynchronously. Once we get the response
    * we will fulfill the promise with the response of the function.
    *
    * Then from the channel extended method [<=] we will get the response of the Future generated previously by the promise.
    */
  @Test
  def asyncStringChannel(): Unit = {
    val channel: Channel[String] = makeChan[String]

    go(() => {
      val newValue = UUID.randomUUID().toString
      s"${Thread.currentThread().getName}-${newValue.toUpperCase}"
    })(channel)

    val (responseFromChannel, error) = <=(channel)
    if (error != null) throw Panic()

    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)
  }

  /**
    * Same example than before but using another Type in the Channel, just to prove the DSL it has and use Strong type system
    */
  @Test
  def asyncFooChannel(): Unit = {
    val channel: Channel[Foo] = makeChan[Foo]

    go(() => {
      Foo(s"${Thread.currentThread().getName}:I'm Foo type")
    })(channel)

    val (responseFromChannel, error) = <=(channel)
    if (error != null) throw Panic()

    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  /**
    * In this example since we dont pass any channel to the [go] routine operator, it wont be response write in the channel
    * and it will be consider a Fire & Forget
    */
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
      val (previousValue, error) = <=(channel)
      if (error != null) throw Panic()
      Foo(previousValue)
    })(channel2, channel1)

    goCompose[Option[Foo], Foo](channel => {
      val (previousValue, error) = <=(channel)
      if (error != null) throw Panic()
      Some(previousValue)
    })(channel3, channel2)

    val (responseFromChannel, error) = <=(channel3)
    if (error != null) throw Panic()

    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)

  }

  /**
    * We can also specify the timeout in the operator to specify how much we want to wait for the response.
    */
  @Test
  def asyncChannelWithDuration(): Unit = {
    val channel: Channel[Foo] = makeChan[Foo]

    go(() => {
      Foo(s"${Thread.currentThread().getName}:With timeout")
    })(channel)

    val (responseFromChannel, error) = <=(channel, 10 seconds)
    if (error != null) throw Panic()

    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)
  }

  /**
    * In this test we prove that once we read a message from the channel the message is gone from
    * the channel and if we try to read the message again we receive the Left side of the tuple
    * with the [NoMoreElements]
    */
  @Test
  def asyncMultipleReadsInChannel(): Unit = {
    val channel: Channel[String] = makeChan[String]

    go(() => {
      val newValue = UUID.randomUUID().toString
      s"${Thread.currentThread().getName}-${newValue.toUpperCase}"
    })(channel)

    val responseFromChannel = <=(channel)
    val responseFromChannel1 = <=(channel)
    val responseFromChannel2 = <=(channel)

    println(s"Main thread:${Thread.currentThread().getName}")
    println(responseFromChannel)
    println(responseFromChannel1)
    println(responseFromChannel2)

  }

  @Test
  def asyncMultipleWritesInChannel(): Unit = {
    val channel: Channel[String] = makeChan[String]

    go(() => {
      val newValue = UUID.randomUUID().toString
      s"${Thread.currentThread().getName}-${newValue.toUpperCase}"
    })(channel)

    val responseFromChannel = <=(channel)
    println(responseFromChannel)

    go(() => {
      val newValue = UUID.randomUUID().toString
      s"${Thread.currentThread().getName}-${newValue.toUpperCase}"
    })(channel)

    val responseFromChannel1 = <=(channel)
    println(responseFromChannel1)

    go(() => {
      val newValue = UUID.randomUUID().toString
      s"${Thread.currentThread().getName}-${newValue.toUpperCase}"
    })(channel)

    val responseFromChannel2 = <=(channel)
    println(responseFromChannel2)

  }

  case class Foo(value: String)

}
