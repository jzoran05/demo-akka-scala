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
  options.addOption("bootstrapServer", true, "example setting: PLAINTEXT://localhost:32769" )

  val parser = new DefaultParser
  val cmd = parser.parse(options, args)


  if(cmd.hasOption("task")) {
    implicit val system = ActorSystem("ActorDemoApplication")
    implicit val materializer = ActorMaterializer()

    val taskName = cmd.getOptionValue("task")
    taskName match {
      case "producer" =>
        exitIfnoBootstrapArg
        startProducer
      case "consumer" =>
        exitIfnoBootstrapArg
        startConsumer
      case "startKafka" => startKafka
      case "basic" => startBasic
    }

    def startKafka: Unit = {
      val kafka = new KafkaContainer
      kafka.start()
      val bootstrapServers = kafka.getBootstrapServers
      println(s"BootstrapServers: $bootstrapServers")
    }

    def exitIfnoBootstrapArg: Unit = {
      if(!cmd.hasOption("bootstrapServer")) {
        println("bootstrapServer arg is missing!")
        this.finalize()
      }
    }


    def startProducer: Unit = {
      try {
        val bootstrapServer = cmd.getOptionValue("bootstrapServer")
        val producerConfig = system.settings.config.getConfig("akka.kafka.producer")
        val producerActor = system.actorOf(KafkaStreamingProducerActor.props(producerConfig, bootstrapServer), "KafkaStreamingProducerActor")

        producerActor ! "gethealth"
        producerActor ! "writekafka"
        producerActor ! "writekafka"
        producerActor ! "writekafka"
        StdIn.readLine
      }
      finally system.terminate
    }

    def startConsumer : Unit = {
      try {

        val bootstrapServers = cmd.getOptionValue("bootstrapServer")
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
