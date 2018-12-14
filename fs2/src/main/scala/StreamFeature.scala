import fs2.Stream
import org.junit.Test
import cats.effect.IO


/**
  * F2s Stream it was an initiative of ScalaZ community to create a reactive Stream for scala.
  * F2s Stream mentioned in this document is NOT scala.collection.immutable.Stream
  * The Stream provide some capabilities such as:
  * * The ability to build arbitrarily complex streams, possibly with embedded effects.
  * * The ability to transform one or more streams using a small but powerful set of operations
  */
class StreamFeature {


  /**
    * To initialize the Stream, we can use operator [empty] in case we dont to emit anything or use [emit]
    */
  @Test
  def mainCreateStream: Unit = {
    println(Stream.empty)
    println(Stream.emit("hello", "Fs2", "stream", "world"))
    println(Stream.emit(List("hello", "Fs2", "stream", "world")))
  }

  /**
    * Just to pass from Stream to elements you can use [toList] and [toVector] between others.
    * Also [Stream] as a monad allow the use of [map] for transformation, and [flatMap] for composition
    * Also as a monad we can use sugar syntax for the [flatMap] with [for comprehension]
    */
  @Test
  def emmitToList: Unit = {
    val listStream = Stream.emit("hello", "Fs2", "stream", "world")
      .toList
    println(listStream)
    val compositionStream = Stream.emit("hello", "Fs2")
      .flatMap(stream => Stream.emit(stream, "stream", "world"))
      .toVector
    println(compositionStream)
    //Sugar composition
    val composedTuplesStream = for {
      tuples <- Stream.emit(1,2,3,4,5)
      composedTuples <- Stream.emit(tuples, 6,7,8,9,10)
    } yield composedTuples
    println(composedTuplesStream.toList)

  }

  /**
    * [Eval] operator creates a single element stream that gets its value by evaluating the supplied effect.
    */
  @Test
  def effects: Unit = {
    val effectStream = Stream.eval(IO[String] {
      println("Hello Stream IO effect")
      "done"
    })
    val emission = effectStream.compile.toList.unsafeRunSync()
    println(emission)
  }


}
