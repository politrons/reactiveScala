package app.impl.zio

import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.junit.Test
import zio.blocking.Blocking
import zio.clock.Clock
import zio.duration._
import zio.kafka.consumer._
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde._
import zio.{Chunk, ZIO}

class ZIOKafkaFeature {

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  val consumerSettings: ConsumerSettings =
    ConsumerSettings(List("localhost:9092"))
      .withGroupId("group")
      .withClientId("client")
      .withCloseTimeout(30.seconds)

  val producerSettings: ProducerSettings = ProducerSettings(List("localhost:9092"))

  val consumer =
    Consumer.make(consumerSettings, Serde.int, Serde.string)

  val consumerAndProducer =
    Consumer.make(consumerSettings, Serde.int, Serde.string) ++
      Producer.make(producerSettings, Serde.int, Serde.string)

  @Test
  def subscribeTopic(): Unit = {
    //Run mock
    KafkaMockServer.start()

    val consumeProduceStream: ZIO[Clock with Blocking with Any, Throwable, Unit] = Consumer
      .subscribeAnd[Any, Int, String](Subscription.topics("zio-topic"))
      .plainStream
      .map { record =>
        println("Record:" + record)
        val key: Int = record.record.key()
        val value: String = record.record.value()
        println(value)
        val producerRecord: ProducerRecord[Int, String] = new ProducerRecord("my-input-topic", key, value)
        (producerRecord, record.offset)
      }
      .chunks
      .mapM { chunk =>
        val records = chunk.map(_._1)
        val offsetBatch = OffsetBatch(chunk.map(_._2).toSeq)

        Producer.produceChunk[Any, Int, String](records) *> offsetBatch.commit
      }
      .runDrain
      .provideSomeLayer(consumerAndProducer)

    val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

    runtime.unsafeRunAsync_(consumeProduceStream)

    //Publish
    publishMessage()

    Thread.sleep(100000)

  }


  def publishMessage(): Unit = {

    val chunkProgram: ZIO[Any with Blocking with Producer[Any, Int, String], Throwable, Chunk[RecordMetadata]] =
      for {
        producerRecord <- ZIO.succeed(new ProducerRecord("zio-topic", 1981, "It's working"))
        task <- Producer.produceChunk[Any, Int, String](Chunk(producerRecord))
        chunk <- task
      } yield chunk


    val value = runtime.unsafeRun(chunkProgram.provideSomeLayer(consumerAndProducer))
    println(value)
  }

}
