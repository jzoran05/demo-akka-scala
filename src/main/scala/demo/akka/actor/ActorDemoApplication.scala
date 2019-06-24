package demo.akka.actor

import akka.actor.{ActorSystem, Props}
import akka.kafka.ConsumerSettings
import akka.stream.ActorMaterializer
import demo.akka.actor.basic.{MyActor, MyActorCompanionObject, MyActorConstructor}
import demo.akka.actor.streamintegration.{KafkaStreamingConsumerActor, KafkaStreamingProducerActor, PrintSomeNumbersActor}
import org.apache.commons.cli.{CommandLineParser, DefaultParser, HelpFormatter, Options}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.testcontainers.containers.KafkaContainer

import scala.io.StdIn

object ActorDemoApplication extends App {

  // create Options object
  val options = new Options()
  options.addOption("task", true, "Task Name: 'producer', 'consumer'" )

  val parser = new DefaultParser
  val cmd = parser.parse(options, args)


  if(cmd.hasOption("task")) {
    implicit val system = ActorSystem("ActorDemoApplication")
    implicit val materializer = ActorMaterializer()

    val taskName = cmd.getOptionValue("task")
    taskName match {
      case "producer" => startProducer
      case "consumer" =>
        if(cmd.hasOption("bootstrapServer")) {
          val bootstrapServer = cmd.getOptionValue("bootstrapServer")
          println(s"bootstrapServer option: $bootstrapServer")
          startConsumer(bootstrapServer)
        } else throw new Exception("bootstrapServer arg is missing!")

      case "producer-consumer" => startProducerConsumer
      case "basic" => startBasic
    }

    def startProducerConsumer: Unit = {
      try {

        val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")
        val producerConfig = system.settings.config.getConfig("akka.kafka.producer")

        val kafka = new KafkaContainer
        kafka.start()
        val bootstrapServers = kafka.getBootstrapServers

        val producerActor = system.actorOf(KafkaStreamingProducerActor.props(producerConfig, bootstrapServers), "KafkaStreamingProducerActor")
        val consumerActor = system.actorOf((KafkaStreamingConsumerActor.props(consumerConfig, bootstrapServers)), "KafkaStreamingConsumerActor")

        producerActor ! "gethealth"
        consumerActor ! "health"

        consumerActor ! "readkafka"

        producerActor ! "writekafka"
        producerActor ! "writekafka"
        producerActor ! "writekafka"
        StdIn.readLine
      }
      finally system.terminate
    }

    def startProducer: Unit = {
      try {

        val producerConfig = system.settings.config.getConfig("akka.kafka.producer")

        val kafka = new KafkaContainer
        kafka.start()
        val bootstrapServers = kafka.getBootstrapServers
        println(s"BootstrapServers: $bootstrapServers")

        val producerActor = system.actorOf(KafkaStreamingProducerActor.props(producerConfig, bootstrapServers), "KafkaStreamingProducerActor")

        producerActor ! "gethealth"
        producerActor ! "writekafka"
        producerActor ! "writekafka"
        producerActor ! "writekafka"
        StdIn.readLine
      }
      finally system.terminate
    }

    def startConsumer(bootstrapServers: String) : Unit = {
      try {

        val consumerConfig = system.settings.config.getConfig("akka.kafka.consumer")
        val consumerActor = system.actorOf((KafkaStreamingConsumerActor.props(consumerConfig, bootstrapServers)), "KafkaStreamingConsumerActor")

        consumerActor ! "health"
        consumerActor ! "readkafka"

        StdIn.readLine
      }
      finally system.terminate
    }

    def startBasic: Unit = {
      try {

        val myActor = system.actorOf(Props[MyActor], "myActor")
        val myActorConstructor = system.actorOf(Props(new MyActorConstructor(myActor)), "myActorConstructor")
        val myActorCompanionObject = system.actorOf(MyActorCompanionObject.props(1), "myActorCompanionObject")
        val printSomeNumbersActor = system.actorOf(PrintSomeNumbersActor.props, "printSomeNumbersActor")

        myActor ! "test"
        myActorConstructor ! "return"
        myActorCompanionObject ! "test"
        printSomeNumbersActor ! "run"

        StdIn.readLine
      }
      finally system.terminate
    }
  } else  {
    val formatter = new HelpFormatter
    formatter.printHelp("akka-demo-scala.App", options)
  }


}
