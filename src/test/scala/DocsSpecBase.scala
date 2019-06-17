/*
 * Copyright (C) 2014 - 2016 Softwaremill <http://softwaremill.com>
 * Copyright (C) 2016 - 2019 Lightbend Inc. <http://www.lightbend.com>
 */

import akka.NotUsed
import akka.kafka.testkit.internal.TestFrameworkInterface
import akka.kafka.testkit.scaladsl.KafkaSpec
import akka.stream.scaladsl.Flow
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FlatSpecLike, Matchers, Suite}

abstract class DocsSpecBase(kafkaPort: Int)
    extends KafkaSpec(kafkaPort)
    with FlatSpecLike
    with TestFrameworkInterface.Scalatest
    with Matchers
    with ScalaFutures
    with Eventually {

  this: Suite =>

  protected def this() = this(kafkaPort = -1)

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(15, Millis)))

  def businessFlow[T]: Flow[T, T, NotUsed] = Flow[T]

}
