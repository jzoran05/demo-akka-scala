import org.scalatest.FunSuite
import org.testcontainers.containers.KafkaContainer
import akka.actor.ActorSystem
import demo.akka.actor.streamintegration.{KafkaStreamingConsumerActor, KafkaStreamingProducerActor}
import org.scalatestplus.junit.AssertionsForJUnit

/*
- Use Kafka testcontainer.org (doesn't require separate zookeeper)
- Consume Kafka stream within Actor
- Process read Kafka messages as messages sent to the Actor from external Service (events, commands)
- Invoke Actor which is consuming Kafka from other actor (send instructions to start, stop reading)
 */
class KafkaStreamingActorTest extends FunSuite with AssertionsForJUnit {


  /*
  Testing starting Kafka packaged by testcontainer.org
   */
  test("TestContainersTest") {
    val kafka = new KafkaContainer
    kafka.start()
    val broker = kafka.getBootstrapServers
    assert(broker != null)
    kafka.stop()
  }

  test("TestAkkaProducerAndConsumer") {

    val system = ActorSystem("test-kafka-system")
    val config = system.settings.config.getConfig("akka.kafka.consumer")

    val kafka = new KafkaContainer
    kafka.start()
    val bootstrapServers = kafka.getBootstrapServers

    val producerActor = system.actorOf(KafkaStreamingProducerActor.props(config, bootstrapServers))
    val consumerActor = system.actorOf(KafkaStreamingConsumerActor.props(config, bootstrapServers))

    producerActor ! KafkaStreamingProducerActor.health // get health
  }


}
