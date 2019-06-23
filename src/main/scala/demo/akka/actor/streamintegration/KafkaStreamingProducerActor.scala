package demo.akka.actor.streamintegration

import akka.actor.Props
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord

object KafkaStreamingProducerActor {
  case object writekafka
  case object gethealth
  def props(config: Config, bootStrapServers: String) : Props =
    Props(new KafkaStreamingProducerActor(config: Config, bootStrapServers))
}

class KafkaStreamingProducerActor (config: Config,
                                   bootStrapServers: String)
  extends AkkaStreamingKafkaAbstractActor  {

  implicit val materializer = ActorMaterializer()
  //private implicit val executionContext = context.system.dispatcher
  val topic = "Topic1"
  val consumerGroup = "Group1"
  var counter = 0

  def receive = {

    case "gethealth" =>
      log.info("'health' invoked")

    case "writekafka" =>
      counter += 1
      log.info(s"'writekafka' invoked $counter times")
      Source.single(counter)
        .map(_.toString)
        .map(value => new ProducerRecord[String, String](topic, value))
        .runWith(Producer.plainSink(createProducerSettings(config, bootStrapServers, consumerGroup)))
      log.info("Completed 'writekafka'")

  }
}
