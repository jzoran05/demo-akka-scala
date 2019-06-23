package demo.akka.actor.streamintegration

import akka.actor.{AbstractActor, Actor, ActorLogging}
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer, StringSerializer}

abstract class AkkaStreamingKafkaAbstractActor()
  extends Actor with ActorLogging {

  def createProducerSettings(
                              config: Config,
                              bootstrapServers: String,
                              consumerGroup: String): ProducerSettings[String, String] = {
    val producerSettings = ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)
    producerSettings
  }

  def createConsumerSettings(
                              config: Config,
                              bootstrapServers: String,
                              consumerGroup: String): ConsumerSettings[String, Array[Byte]] = {

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

}
