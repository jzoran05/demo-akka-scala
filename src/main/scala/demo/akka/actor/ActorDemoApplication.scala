package demo.akka.actor

import akka.actor.{ActorSystem, Props}
import akka.kafka.ConsumerSettings
import akka.stream.ActorMaterializer
import demo.akka.actor.basic.{MyActor, MyActorCompanionObject, MyActorConstructor}
import demo.akka.actor.streamintegration.{KafkaStreamingProducerActor, PrintSomeNumbersActor}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.testcontainers.containers.KafkaContainer

import scala.io.StdIn

object ActorDemoApplication extends App {

  implicit val system = ActorSystem("ActorDemoApplication")

  try {

    implicit val materializer = ActorMaterializer()
    val config = system.settings.config.getConfig("akka.kafka.consumer")

    val myActor = system.actorOf(Props[MyActor], "myActor")
    val myActorConstructor = system.actorOf(Props(new MyActorConstructor(myActor)), "myActorConstructor")
    val myActorCompanionObject = system.actorOf(MyActorCompanionObject.props(1), "myActorCompanionObject")
    val printSomeNumbersActor = system.actorOf(PrintSomeNumbersActor.props, "printSomeNumbersActor")

    //val config = system.settings.config.getConfig("akka.kafka.consumer")

    val kafka = new KafkaContainer
    kafka.start()
    val bootstrapServers = kafka.getBootstrapServers

    val producerActor = system.actorOf(KafkaStreamingProducerActor.props(config, bootstrapServers))

    myActor ! "test"
    myActorConstructor ! "return"
    myActorCompanionObject ! "test"
    printSomeNumbersActor ! "run"

    StdIn.readLine()
  }  finally system.terminate()
}
