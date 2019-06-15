package demo.akka.actor.basic

import akka.actor.{Actor, ActorLogging, Props}

object MyActorCompanionObject {
  def props(initCounter: Int): Props = Props(new MyActorCompanionObject(initCounter))
}

class MyActorCompanionObject(initCounter: Int) extends Actor with ActorLogging{
  def receive = {
    case "test" => {
      log.info(s"'test' invoked with $initCounter")
    }
  }
}
