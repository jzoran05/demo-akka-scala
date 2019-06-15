package demo.akka.actor

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import demo.akka.actor.basic.{MyActor, MyActorCompanionObject, MyActorConstructor}
import demo.akka.actor.streamintegration.PrintSomeNumbersActor

import scala.io.StdIn

object ActorDemoApplication extends App {

  implicit val system = ActorSystem("ActorDemoApplication")

  try {

    implicit val materializer = ActorMaterializer()
    val myActor = system.actorOf(Props[MyActor], "myActor")
    val myActorConstructor = system.actorOf(Props(new MyActorConstructor(myActor)), "myActorConstructor")
    val myActorCompanionObject = system.actorOf(MyActorCompanionObject.props(1), "myActorCompanionObject")
    val printSomeNumbersActor = system.actorOf(PrintSomeNumbersActor.props, "printSomeNumbersActor")
    myActor ! "test"
    myActorConstructor ! "return"
    myActorCompanionObject ! "test"
    printSomeNumbersActor ! "run"
    StdIn.readLine()
  }  finally system.terminate()
}
