package demo.akka.actor.streamintegration

import akka.actor.{Actor, Props}
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{MergeHub, Source}
import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerRecord

// scala companion object
object KafkaStreamingDynamicProducerActor {
  def props(config: Config, bootStrapServers: String) : Props =
    Props(new KafkaStreamingDynamicProducerActor(config, bootStrapServers))
}

/*
Implement https://doc.akka.io/docs/akka/current/stream/stream-dynamic.html
 */
class KafkaStreamingDynamicProducerActor(config: Config, bootStrapServers: String) extends AkkaStreamingKafkaAbstractActor {

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val topic = "Topic1"
  val consumerGroup = "Group1"
  var counter = 0
  private val consumer = Producer.plainSink(createProducerSettings(config, bootStrapServers, consumerGroup))
  private val runnableGraph = MergeHub.source(perProducerBufferSize = 5).to(consumer)
  private val toConsumer = runnableGraph.run()

  def receive: Receive = {

    case "send" =>
      counter += 1
      Source.single(counter)
        .map(_.toString)
        .map(value => new ProducerRecord[String, String](topic, value))
        .runWith(toConsumer)

  }
}
