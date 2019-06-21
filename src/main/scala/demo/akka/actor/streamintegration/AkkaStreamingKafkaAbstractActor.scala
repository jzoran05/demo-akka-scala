package demo.akka.actor.streamintegration

import akka.actor.{AbstractActor, Actor, ActorLogging}
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer, StringSerializer}

abstract class AkkaStreamingKafkaAbstractActor(config: Config)
  extends Actor with ActorLogging {

  def createProducerSettings(bootstrapServers: String, consumerGroup: String): ProducerSettings[String, String] = {
    val producerSettings = ProducerSettings(config, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)
    producerSettings
  }

  def createConsumerSettings(bootstrapServers: String, consumerGroup: String): ConsumerSettings[String, Array[Byte]] = {

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
