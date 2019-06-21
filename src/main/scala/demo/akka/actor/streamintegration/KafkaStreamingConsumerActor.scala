package demo.akka.actor.streamintegration

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigMergeable
import java.util.Optional
import java.util.concurrent.atomic.AtomicLong

import akka.Done
import akka.actor.ActorSystem
import akka.annotation.InternalApi
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.internal._
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.{Keep, Sink}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}

import scala.concurrent.Future
//import akka.util.JavaDurationConverters._
import com.typesafe.config.Config
import akka.kafka.scaladsl.{Consumer, Producer}

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.duration._

/*
Companion object
 */
object KafkaStreamingConsumerActor {
  case object readkafka
  case object health
  def props(config: Config,
            bootStrapServers: String) : Props = Props(new KafkaStreamingConsumerActor(config, bootStrapServers))
}


class KafkaStreamingConsumerActor(config: Config,
                                  bootStrapServers: String) extends AkkaStreamingKafkaAbstractActor(config) {

  private implicit val executionContext = context.system.dispatcher
  private implicit val materializer = ActorMaterializer()
  val topic = "Topic1"
  val consumerGroup = "Group1"
  private val akkaStreamKafkaSource = Consumer.plainSource(
    createConsumerSettings(bootStrapServers, consumerGroup),
    Subscriptions.topics(topic))
      .map(_.value().toString)
  private val sink = Sink.foreach[String](println(_))

  def receive = {

    case health =>
      log.info("'health' invoked")

    case "readkafka" =>
      log.info("'readkafka' invoked")
      val runnable = akkaStreamKafkaSource.toMat(sink)(Keep.right)
      runnable.run
      log.info("Completed 'readkafka'")
  }

/*
  private def createConsumerSettings(bootstrapServers: String, consumerGroup: String): ConsumerSettings[String, Array[Byte]] = {

    if (config == null) log.error("'config' is not initialized")
    // #settings
    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withGroupId(consumerGroup)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    //#settings
    consumerSettings
  }
*/


}
