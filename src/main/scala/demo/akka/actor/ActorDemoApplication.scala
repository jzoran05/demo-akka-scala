package demo.akka.actor

import akka.actor.{ActorSystem, Props}
import akka.kafka.ConsumerSettings
import akka.stream.ActorMaterializer
import demo.akka.actor.basic.{MyActor, MyActorCompanionObject, MyActorConstructor}
import demo.akka.actor.streamintegration.{KafkaStreamingActor, PrintSomeNumbersActor}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

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
    val kafkaStreamingActor = system.actorOf(KafkaStreamingActor.props(materializer, config))


    myActor ! "test"
    myActorConstructor ! "return"
    myActorCompanionObject ! "test"
    printSomeNumbersActor ! "run"
    kafkaStreamingActor ! "readkafka"

    StdIn.readLine()
  }  finally system.terminate()
}
