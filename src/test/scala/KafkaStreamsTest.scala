import java.util.concurrent.atomic.AtomicLong

import akka.Done
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.testkit.scaladsl.TestcontainersKafkaLike
import akka.stream.scaladsl.{Keep, Sink, Source}
import org.apache.kafka.clients.consumer.internals.PartitionAssignor.Subscription
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer, StringSerializer}
import org.scalatest.FunSuite
import scala.concurrent.duration._
import scala.concurrent.Future


class KafkaStreamsTest extends DocsSpecBase with TestcontainersKafkaLike {

  private def waitBeforeValidation(): Unit = sleep(6.seconds)

  "test1" should "work" in {
    val consumerSettings = createConsumerSettings().withGroupId(createGroupId())
    val producerSettings = producerDefaults // createProducerSettings()
    val topic = createTopic()

    val kafkaProduce = Source(1 to 10)
        .map(_.toString)
        .map(value => new ProducerRecord[String, String](topic, value))
        .runWith(Producer.plainSink(producerSettings))

    val source = Consumer.plainSource(consumerSettings, Subscriptions.topics(topic)).map(_.value().toString)
    val sink = Sink.foreach[String](println(_))
    val runnable = source.toMat(sink)(Keep.right)
    runnable.run

    waitBeforeValidation()
    kafkaProduce.futureValue should be(Done)
    //runnable.shutdown().futureValue should be(Done)
    //result.futureValue should have size (100)
  }

  private def createProducerSettings(): ProducerSettings[String, String] = {
    val config = system.settings.config.getConfig("akka.kafka.consumer")
    val producerSettings = ProducerSettings(config, new StringSerializer, new StringSerializer)
        .withBootstrapServers(bootstrapServers)
    producerSettings
  }

  private def createConsumerSettings(): ConsumerSettings[String, Array[Byte]] = {
    // #settings
    val config = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withGroupId("group1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    //#settings
    consumerSettings
  }

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

}
