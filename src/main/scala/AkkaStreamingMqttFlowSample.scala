import akka.stream.{ActorMaterializer, Materializer}
import scala.concurrent.ExecutionContext
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.alpakka.mqtt.streaming._
import akka.stream.alpakka.mqtt.streaming.scaladsl.{ActorMqttClientSession, ActorMqttServerSession, Mqtt}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, Sink, Source, SourceQueueWithComplete, Tcp}
import akka.stream._
import akka.util.ByteString
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration._

class AkkaStreamingMqttFlowSample {


  private implicit val mat: Materializer = ActorMaterializer()
  private implicit val dispatcherExecutionContext: ExecutionContext = system.dispatcher

}
