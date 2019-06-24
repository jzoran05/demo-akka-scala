package demo.akka.actor.streamintegration

import akka.actor.Props
import akka.stream.ActorMaterializer
import akka.kafka.{ProducerMessage, Subscriptions}
import akka.stream.scaladsl.{Keep, Sink}
import com.typesafe.config.Config
import akka.kafka.scaladsl.Consumer


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
  }




}
