import cats.effect.{Async, ContextShift, IO}
import fs2.Stream
import org.junit.Test

/**
  * F2s Stream it was an initiative of ScalaZ community to create a reactive Stream for scala.
  * F2s Stream mentioned in this document is NOT scala.collection.immutable.Stream
  * The Stream provide some capabilities such as:
  * * The ability to build arbitrarily complex streams, possibly with embedded effects.
  * * The ability to transform one or more streams using a small but powerful set of operations
  */
class StreamFeature {

  /** ------------------- */
  /** CREATION      */
  /** ------------------- */
  /**
    * To initialize the Stream, we can use operator:
    * * [empty] in case we dont to emit anything
    * * [emit] to start the emission in eager way and create the pipeline with the value right away.
    * * [emits] a Seq of elements in the pipeline.
    * * [eval] to create a lazy value which it will evaluated and become impure once we compile and use [unsafeRunSync]
    * * [range] to create a stream that emit from A to B range
    * * [raiseError] create an Stream that emit an exception.
    */
  @Test
  def mainCreateStream: Unit = {
    println(Stream.empty)
    println(Stream.emit("hello", "Fs2", "stream", "world"))
    println(Stream.emits(List("hello", "Fs2", "stream", "world")))
    println(Stream.eval(IO {
      List("hello", "Fs2", "Pure", "world")
    }))
    println(Stream.range(1, 10))
    println(Stream.raiseError[IO](new Exception("Controlled exception"))
    )
  }

  /** ------------------- */
  /** COMPOSITION   */
  /** ------------------- */
  /**
    * Just to pass from Stream to elements you can use [toList] and [toVector] between others.
    * Also [Stream] as a monad allow the use of [map] for transformation, and [flatMap] for composition
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

  }

  /**
    * Also as a monad we can use sugar syntax for the [flatMap] with [for comprehension]
    */
  @Test
  def emmitToListSugar: Unit = {
    val composedTuplesStream = for {
      tuples <- Stream.emit(1, 2, 3, 4, 5)
      composedTuples <- Stream.emit(tuples, 6, 7, 8, 9, 10)
    } yield composedTuples
    println(composedTuplesStream.toList)
  }

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.Implicits.global)

  /**
    * [Merge] operator allow parallelism works when we are emitting elements, so in this case here we emmit elements
    * from the first collection but we merge with the other two Streams running async, once they finish.
    * In order to make the merge operator work in another thread, just like in Scala future we need to provide an implicit [ExecutionContext]
    */
  @Test
  def mergeStreams: Unit = {
    val mergedValues = Stream("hello", "async")
      .merge(Stream.eval(IO {
        println(s"Process in Thread:${Thread.currentThread().getName}")
        Thread.sleep(500)
        "Fs2"
      }))
      .merge(Stream.eval(IO {
        println(s"Process in Thread:${Thread.currentThread().getName}")
        Thread.sleep(500)
        "World"
      }))
      .map(value => value.toUpperCase)
      .compile.toVector.unsafeRunSync()
    println(mergedValues)
  }

  /** ------------------- */
  /** EFFECTS       */
  /** ------------------- */
  /**
    * [Eval] operator creates a single element stream that gets its value by evaluating the supplied effect.
    * For the effect we use IO monad, similar to Haskell IO, it wrap the emission in this monad and control the effects.
    * The great feature of eval is that evaluate the IO which is [lazy] and become the value to be emitted through the pipeline
    * once the [compile] operator and [unsafeRunSync] is used.
    * In this example we use [flatMap] to compose both Streams as we did before, but now you can see we are not
    * composing IO but Strings.
    */
  @Test
  def evalEffects: Unit = {
    val effectStream = Stream.eval(
      IO[String] {
        println("Process context 1")
        "Hello_Stream"
      }.map(word => word.replace("_", " "))
    )
      .flatMap(word => Stream.eval(IO {
        println("Process context 2")
        " IO effect"
      }
        .map(word2 => word.concat(word2))))
      .map(value => value.toUpperCase())
    val emission = effectStream.compile.toList.unsafeRunSync()
    println(emission)
  }

  /**
    * Again if you rather make the composition using sugar with [for comprehension] you can do it
    * Nothing new but just to remind you, in case you want to add an operation in the for comprenhension
    * it must being a computation done in the same monad type [Stream]
    */
  @Test
  def evalEffectsSugar: Unit = {
    val effectStream = for {
      word <- Stream.eval(
        IO[String] {
          println("Process context 1")
          "Hello_Stream"
        }.map(word => word.replace("_", " "))
      )
      word1 <- Stream.eval(IO {
        println("Process context 2")
        " IO effect"
      })
      finalWord <- Stream.eval(IO {
        word.concat(word1)
      })
    } yield finalWord.toUpperCase
    val emission = effectStream.compile.toList.unsafeRunSync()
    println(emission)
  }

  /** ------------------------ */
  /** ERROR HANDLING     */
  /** ------------------------ */
  /**
    * Error handling [handleErrorWith] operator, just work as Scala future [recovery] or ReactiveX [onErrorResponse]
    * They catch the error throw in the pipeline and allow you to give you the chance to control the effect
    * and return a Control error response.
    */
  @Test
  def errorHandling: Unit = {
    val errorResponse = Stream.eval(IO(throw new Exception("ouch ouch ouch")))
      .handleErrorWith(e => Stream.emit(e.getMessage))
      .map(value => value.toUpperCase())
      .flatMap(value => Stream.emit(value.concat(".Thank god I had a net here")))
      .compile
      .toList
      .unsafeRunSync()
    println(errorResponse)
  }

  /**
    * Stream also provide the operator [raiseError] to throw an Exception in the pipeline.
    * Pretty handy when you do composition if you want to throw a business exception that your
    * Error handling will catch and evaluate.
    */
  @Test
  def errorHandlingThrowError: Unit = {
    val err = Stream.emit(math.random < 0.50)
      .flatMap(number => if (number) {
        Stream.raiseError[IO](new IllegalArgumentException("Illegal number found"))
      } else {
        Stream.emit(number)
      })
      .handleErrorWith(e => Stream.emit(e.getMessage))
      .compile
      .toList
      .unsafeRunSync()
    print(err)
  }

  /** ---------- */
  /** RESOURCES **/
  /** ---------- */

  /**
    * [Bracket] operator creates a stream that emits a resource allocated by an effect, ensuring the resource is
    * eventually released regardless of how the stream is used.
    * Bracket use curly to create a function that receive a monad (acquire:F[R]) and then the (release: R => F[Unit]) function
    * which is invoked once the result emitted by [acquire] Stream terminates.
    */
  @Test
  def controlResource: Unit = {
    val count = new java.util.concurrent.atomic.AtomicLong(0)
    val acquire = IO {
      val incrementNum = count.incrementAndGet
      println("Before acquire resource: " + incrementNum)
      incrementNum
    }
    val release = IO {
      println("After release resource: " + count.decrementAndGet);
      ()
    }
    Stream.bracket(acquire)(_ => release)
      .flatMap(num => Stream(100).map(num1 => num + num1))
      .map(value => {
        println(s"Number processed:$value")
        value
      })
      .compile.drain.unsafeRunSync()
  }

  /** ---------------- **/
  /** UTILS OPERATORS  **/
  /** ---------------- **/

  /**
    * Just like other Streams solutions Fs2 keep most of the most handy operators to interact with.
    * * [take] operator take first N elements from the emission of the pipeline.
    * * [drop] operator drop first N elements from the emission of the pipeline.
    * * [takeWhile] operator it will take elements emitted while the predicate function apply
    * * [scan] behave just like left fold, we give an initial value and then pass a tuple to a function with the new emitted
    * value, and also the previous iteration value.
    */
  @Test
  def utilsOperators: Unit = {
    val takeList = Stream.emits(List("hello", "Fs2", "stream", "world"))
      .take(2)
      .toList
    println(takeList)
    val droppedList = Stream.emits(List("hello", "Fs2", "stream", "world"))
      .drop(2)
      .toList
    println(droppedList)
    val takeWhileList = Stream.emits(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
      .takeWhile(num => num <= 5)
      .toList
    println(takeWhileList)
    val scanList = Stream.range(1, 10).scan(0)((oldValue, newValue) => oldValue + newValue).toList
    println(scanList)

  }

}
