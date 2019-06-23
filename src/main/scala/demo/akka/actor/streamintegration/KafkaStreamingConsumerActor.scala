package demo.akka.actor.streamintegration

import akka.actor.Props
import akka.stream.ActorMaterializer
import akka.kafka.{ProducerMessage, Subscriptions}
import akka.stream.scaladsl.{Keep, Sink}
import com.typesafe.config.Config
import akka.kafka.scaladsl.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

/*
Companion object
 */
object KafkaStreamingConsumerActor {
  case object readkafka
  case object health
  def props(config: Config, bootStrapServers: String) : Props =
    Props(new KafkaStreamingConsumerActor(config: Config, bootStrapServers))
}


class KafkaStreamingConsumerActor(config: Config, bootStrapServers: String) extends AkkaStreamingKafkaAbstractActor {

  private implicit val executionContext = context.system.dispatcher
  private implicit val materializer = ActorMaterializer()

  val topic = "Topic1"
  val consumerGroup = "Group1"

  private val akkaStreamKafkaSource = Consumer.plainSource(
    createConsumerSettings(config, bootStrapServers, consumerGroup),
    Subscriptions.topics(topic))
      .map ( msg => new String(msg.value()) )

  private val sink = Sink.foreach[String](println(_))

  def receive = {

    case "health" =>
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
