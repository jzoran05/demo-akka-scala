import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.FunSuite
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.rnorth.ducttape.unreliables.Unreliables
import java.util
import java.util.UUID
import java.util.concurrent.TimeUnit
import org.scalatestplus.junit.AssertionsForJUnit


class KafkaStreamingActorTest extends FunSuite with AssertionsForJUnit {
  test("Test1") {
    val kafka = new KafkaContainer
    kafka.start()
    val broker = kafka.getBootstrapServers
    assert(broker != null)

    testKafkaFunctionality(kafka.getBootstrapServers)
  }



  @throws[Exception]
  protected def testKafkaFunctionality(bootstrapServers: String): Unit = {


    try {

      val serverConfig = ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
      val producerConfig = ProducerConfig.CLIENT_ID_CONFIG

      val producerConfigMap: ImmutableMap[String, AnyRef] = ImmutableMap.of(
        serverConfig, bootstrapServers,
        producerConfig, UUID.randomUUID.toString)

      val producer = new KafkaProducer[String, String](producerConfigMap, new StringSerializer, new StringSerializer)

      val consumerConfigMap: ImmutableMap[String, AnyRef] = ImmutableMap.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
        ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      val consumer = new KafkaConsumer[String, String](consumerConfigMap, new StringDeserializer, new StringDeserializer)

      try {
        val topicName = "messages"
        consumer.subscribe(util.Arrays.asList(topicName))
        producer.send(new ProducerRecord[String, String](topicName, "testcontainers", "rulezzz")).get
        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () => {
          def foo(): Boolean = {
            val records = consumer.poll(100)
            if (records.isEmpty) return false
            assert(records.count() == 1)
            records.forEach {
              println
            }
            //assertThat(records).hasSize(1).extracting(ConsumerRecord.topic, ConsumerRecord.key, ConsumerRecord.value).containsExactly(tuple(topicName, "testcontainers", "rulezzz"))
            true
          }

          foo()
        })
        consumer.unsubscribe()
      } finally {
        if (producer != null) producer.close()
        if (consumer != null) consumer.close()
      }
    }
  }

}
