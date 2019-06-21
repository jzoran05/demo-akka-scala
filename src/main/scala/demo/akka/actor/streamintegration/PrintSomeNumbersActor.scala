package demo.akka.actor.streamintegration

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ActorMaterializer
import akka.stream._
import akka.stream.scaladsl._
//import akka.stream.scaladsl.JavaFlowSupport.Source

object PrintSomeNumbersActor {
  def props() : Props = Props(new PrintSomeNumbersActor())
}

class PrintSomeNumbersActor() extends Actor with ActorLogging {

  private implicit val executionContext = context.system.dispatcher
  implicit val materializer = ActorMaterializer()
  def receive = {
    case "run" => {
      Source(1 to 10)
        .map(_.toString)
        .runForeach(println)
        .map(_=> self ! "done")
      log.info("done")
      //context.stop(self)
    }
  }
}
