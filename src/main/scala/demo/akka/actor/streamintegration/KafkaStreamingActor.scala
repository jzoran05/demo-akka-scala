package demo.akka.actor.streamintegration

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigMergeable
import java.util.Optional
import java.util.concurrent.atomic.AtomicLong

import akka.Done
import akka.actor.ActorSystem
import akka.annotation.InternalApi
import akka.kafka.ConsumerSettings
import akka.kafka.internal._
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.Future
//import akka.util.JavaDurationConverters._
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.{Consumer, ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, Deserializer, StringDeserializer}

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.duration._


class OffsetStore {
  // #plainSource

  private val offset = new AtomicLong

  // #plainSource
  def businessLogicAndStoreOffset(record: ConsumerRecord[String, String]): Future[Done] = // ...
  // #plainSource
  {
    println(s"DB.save: ${record.value}")
    offset.set(record.offset)
    Future.successful(Done)
  }

  // #plainSource
  def loadOffset(): Future[Long] = // ...
  // #plainSource
    Future.successful(offset.get)

  // #plainSource
}

object KafkaStreamingActor {
  def props(implicit materializer: ActorMaterializer, config: Config) : Props = Props(new KafkaStreamingActor())
}


class KafkaStreamingActor(implicit materializer: ActorMaterializer, config: Config) extends Actor with ActorLogging {

  private implicit val executionContext = context.system.dispatcher

  def receive = {
    case "readkafka" => {


    }
  }

  def createSettings(): ConsumerSettings[String, Array[Byte]] = {
    // #settings
    val bootstrapServers: String = "<kafka broker endpoint>"

    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    //#settings
    consumerSettings
  }

  def createAutoCommitSettings(): ConsumerSettings[String, Array[Byte]] = {
    val consumerSettings = createSettings()
    val consumerSettingsWithAutoCommit =
    // #settings-autocommit
      consumerSettings
        .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
        .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000")
    // #settings-autocommit
    consumerSettingsWithAutoCommit
  }

}
