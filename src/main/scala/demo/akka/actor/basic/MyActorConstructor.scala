package demo.akka.actor.basic

import akka.actor.{Actor, ActorLogging, ActorRef}

class MyActorConstructor(sender: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case "return" => {
      log.info(s"'return' invoked.")
      sender ! "response"
    }
    case "kill" => {
      log.info("'kill' invoked")
    }
  }
}
