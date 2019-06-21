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
  case object health
  def props(config: Config,
            bootStrapServers: String) : Props = Props(new KafkaStreamingProducerActor(config, bootStrapServers))
}

class KafkaStreamingProducerActor (config: Config,
                                   bootStrapServers: String)
  extends AkkaStreamingKafkaAbstractActor (config) {

  implicit val materializer = ActorMaterializer()
  //private implicit val executionContext = context.system.dispatcher
  val topic = "Topic1"
  val consumerGroup = "Group1"
  var counter = 0

  def receive = {

    case health =>
      log.info("'health' invoked")

    case "writekafka" =>
      log.info("'writekafka' invoked")
      counter += 1
      Source.single(counter)
        .map(_.toString)
        .map(value => new ProducerRecord[String, String](topic, value))
        .runWith(Producer.plainSink(createProducerSettings(bootStrapServers, consumerGroup)))
      log.info("Completed 'writekafka'")
  }
}
