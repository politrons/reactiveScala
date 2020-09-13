package app.impl.zio

import org.junit.Test
import scalaz.zio._
import scalaz.zio.stream._

import scala.util.Random

/**
  * ZIO ZStream works quite similar like Akka Stream DSL does. Here we show examples of the DSL to create
  * Streams and Sinks, and how they work together.
  */
class ZIOZStream extends DefaultRuntime {

  /**
    * Stream
    * -------
    * DSL of ZStream to create the Streams from different values or possible effects.
    */
  /**
    * We have a [Stream] type which it can be created using [ZStream] factory from iterator using [fromIterable]
    * We also create a [Sink] using [ZSink] factory where using [foldLeft] we can get all elements of stream
    * and concat together.
    * Finally to create our [ZIO] program we just use [run] operator to combine Stream and Sink
    */
  @Test
  def streamFromIterable(): Unit = {
    val zStream: stream.Stream[Nothing, String] =
      ZStream.fromIterable(List("Hello", "Stream", "world", "in", "zio", "@"))
        .map(element => element.toUpperCase)
        .filter(value => value.length > 1)

    val zSink: ZSink[Any, Nothing, Nothing, String, String] =
      ZSink.foldLeft(new String())((prev, next) => prev ++ " " ++ next)

    val stringProgram: ZIO[Any, Nothing, String] = zStream.run(zSink)

    println(unsafeRun(stringProgram))

  }

  /**
    * We can create a Stream from a single value using apply [Stream], then using using [Sink.identity]
    * and transforming into Optional with optional, we will return a program with Option[T] as output
    */
  @Test
  def streamSingle(): Unit = {
    val maybeProgram: ZIO[Any, Nothing, Option[String]] = ZStream("Single element in pipeline")
      .map(value => value.toUpperCase())
      .run(Sink.identity[String].optional)

    println(unsafeRun(maybeProgram))

  }

  /**
    * In case we detect that our Stream source it can have an effect, we control it using operator
    * [fromEffect] which expect a ZIO program where it specify which throwable effect it might have.
    * Then automatically our ZStream it will have the effect type passed from the ZIO program mentioned.
    * Once that happens we can control that effect in our program as usual, using catchAll.
    */
  @Test
  def streamEffect(): Unit = {
    val maybeProgram: ZIO[Any, Throwable, Option[String]] =
      ZStream.fromEffect(ZIO.effect {
        if (Random.nextBoolean()) "Effect element in pipeline" else throw new NullPointerException
      })
        .map(value => value.toUpperCase())
        .run(Sink.identity[String].optional)
        .catchAll(t => ZIO.succeed(Some(s"Error detected in pipeline. Caused by ${t.getMessage}")))

    println(unsafeRun(maybeProgram))

  }

  /**
    * A way to continue consuming forever and never end a program stream, is to consume from a Queue, something quite
    * similar as how Akka actor works. Here we create a Queue we pass to the Stream, and we specify that the stream
    * it will work [forever] in another fiber [fork] then, run the program and it return the Queue.
    * Once we have the Queue we dont have to do more than just push elements in it, using [offer] operator,
    *
    */
  @Test
  def streamQueue(): Unit = {
    val inputQueue = unsafeRun {
      for {
        queue <- ZQueue.bounded[String](100)
        _ <- ZStream.fromQueue[String](queue)
          .map(value => value.toUpperCase)
          .map(value => println(value))
          .run(Sink.drain)
          .forever.fork
      } yield queue
    }
    unsafeRun(inputQueue.offer("hello"))
    unsafeRun(inputQueue.offer("zio"))
    unsafeRun(inputQueue.offer("stream"))
    unsafeRun(inputQueue.offer("world"))

    Thread.sleep(1000)
  }

  /**
    * Merge operator allow us to merge different Stream outputs into just one, one of the coolest thing about this merge
    * operator is that allow merge together different types after being processed.
    */
  @Test
  def streamMerge(): Unit = {
    val mergeProgram =
      ZStream("Hello")
        .merge(ZStream("Zio").map(value => value.toUpperCase))
        .merge(ZStream(2.0))
        .merge(ZStream("stream").map(value => value.concat("!")))
        .merge(ZStream(981).map(value => value + 1000))
        .map(value => s"@$value@")
        .run(Sink.collect[String])
    println(unsafeRun(mergeProgram))

  }

  /**
    * Sink
    * ------
    * The DSL of the end of the pipeline once we want to transform the Stream into ZIO program output.
    */

  /**
    * Using ZSink operator identity[T].optional we're able to return the head element of the iterable, as Optional
    * or None
    */
  @Test
  def sinkOptional(): Unit = {
    val sink = Sink.identity[String].optional
    val maybeProgram: ZIO[Any, Nothing, Option[String]] =
      ZStream.fromIterable(List())
        .run(sink)
    println(unsafeRun(maybeProgram))
  }

  /**
    * Using ZSink operator [readWhile] we're able passing a predicate function to filter the elements emitted in the pipeline.
    */
  @Test
  def sinkReadWhile(): Unit = {
    val sink: ZSink[Any, Nothing, Int, Int, List[Int]] = Sink.readWhile[Int](element => element < 5)
    val smallNumbersProgram: ZIO[Any, Nothing, List[Int]] =
      ZStream.fromIterable(List(1, 2, 3, 4, 5, 6, 7))
        .run(sink)
    println(unsafeRun(smallNumbersProgram))
  }

  /**
    * Sink it's quite interesting operator, using together with another sink, we're able to race multiple Sinks
    * and the one that wins it provide the result of the program.
    */
  @Test
  def sinkRace(): Unit = {
    val sink1 = Sink.fromFunction[String, String](_ => {
      Thread.sleep(1000)
      "First sink win"
    })
    val sink2 = Sink.fromFunction[String, String](_ => {
      Thread.sleep(1)
      "Second sink win"
    })
    val raceSink = sink1.race(sink2)
    val raceProgram =
      ZStream("Ladies and Gentleman, start your engines")
        .run(raceSink)
    println(unsafeRun(raceProgram))
  }

}
