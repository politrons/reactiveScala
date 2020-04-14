package app.impl.zio


import java.nio.file.Paths
import net.manub.embeddedkafka.ops.ZooKeeperOps
import net.manub.embeddedkafka.{EmbeddedK, EmbeddedKafka, EmbeddedKafkaConfig, EmbeddedZ}
import scala.reflect.io.Directory
import scala.util.{Success, Try}

trait KafkaMockServer extends ZooKeeperOps with EmbeddedKafka {
  lazy val kafkaPort = sys.props.getOrElse("kafkaMockPort", "9092").toInt
  lazy val zkPort: Int = sys.props.getOrElse("zookeeperMockPort", "2300").toInt
  lazy val bootstrapServers = s"localhost:$kafkaPort"

  lazy val KafkaLogsDir = "target/logs/kafka"
  lazy val ZookeeperLogsDir = "target/logs/zookeeper"

}

object KafkaMockServer extends KafkaMockServer {
  def start(): Option[EmbeddedK] = {
    val kafkaDir = Paths.get(KafkaLogsDir)
    val zkLogsDir = Paths.get(ZookeeperLogsDir)
    val brokerProperties = Map("log.dir" -> kafkaDir.toFile().getAbsolutePath)

    implicit val config = EmbeddedKafkaConfig(kafkaPort, zkPort, brokerProperties)

    val factory = EmbeddedZ(startZooKeeper(config.zooKeeperPort, zkLogsDir), zkLogsDir)

    Try(Some(EmbeddedKafka.startKafka(kafkaDir, Option(factory)))) match {
      case Success(server) => server
      case _ => None
    }
  }

  def stop() = {
    EmbeddedKafka.stop()
  }

  def isRunning() = EmbeddedKafka.isRunning
}
