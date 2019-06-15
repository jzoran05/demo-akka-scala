package demo.akka.actor.basic

import akka.actor.Actor
import akka.event.Logging

class MyActor extends Actor {
  val log = Logging(context.system, this)
  var counter = 0

  override def preStart(): Unit = log.info("MyActor started")
  override def postStop(): Unit = log.info("MyActor stopped")

  def receive = {
    case "test" => {
      counter += 1
      log.info(s"received test $counter number of times")
    }
    case "response" => {
      log.info("'response' invoked")
    }
    case _      => log.info("received unknown message")
  }
}
