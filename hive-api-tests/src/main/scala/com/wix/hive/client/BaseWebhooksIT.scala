package com.wix.hive.client

import java.util.UUID

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Duration
import com.wix.hive.client.infrastructure.WebhooksDriver
import com.wix.hive.server.FinagleWebhooksWebServer
import com.wix.hive.server.webhooks.{Provision, Webhook, WebhookParameters}
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.specs2.execute.AsResult._
import org.specs2.matcher.Matcher
import org.specs2.mock.Mockito
import org.specs2.specification.Before

import scala.util.Try

/**
 * User: maximn
 * Date: 12/2/14
 */
abstract class BaseWebhooksIT
  extends BaseIT
  with Mockito {
  self: WebhooksDriver =>

  val port: Int = 8001
  val path: String = "localhost/webhook-url/"
  val secret: String = "e5f5250a-dbd0-42a1-baf9-c61ea20c401b"

  override def initEnv(): Unit = srv.start()

  override def beforeTest(): Unit = reset(mockFunc)

  override def shutdownEnv(): Unit = srv.stop(Duration.fromMilliseconds(500))

  val mockFunc = mock[Try[Webhook[_]] => Unit]

  val key = "e5f5250a-dbd0-42a1-baf9-c61ea20c401b"

  val srv = new FinagleWebhooksWebServer(8001, key) {
    override def onReq(webhook: Try[Webhook[_]]): Unit = mockFunc(webhook)
  }

  step(initEnv())


  trait ctx extends Before
  with HiveCommandsMatchers {
    override def before: Any = beforeTest()

    val timeout = new org.specs2.time.Duration(2000)

    val client: Service[HttpRequest, HttpResponse] = Http.newService("localhost:8001")

    val appId = UUID.randomUUID().toString
    val instanceId = UUID.randomUUID().toString
    val timestamp = new DateTime(2014, 2, 11, 1, 2)

    def aProvisionData(instanceId: String = instanceId) = Provision(instanceId, None)

    def aWebhookParams(appId: String = appId, timestamp: DateTime = timestamp) = WebhookParameters(appId, timestamp)

    def aProvisionWebhook(instanceId: String = instanceId) = Webhook(instanceId, aProvisionData(instanceId), aWebhookParams())


    def beProvisionWebhook(instanceId: Matcher[String], appId: Matcher[String], originInstanceId: Matcher[Option[String]] = beNone): Matcher[Webhook[Provision]] = {
      instanceId ^^ {(_: Webhook[Provision]).instanceId aka "instanceId"} and
        instanceId ^^ {(_: Webhook[Provision]).data.instanceId aka "provision.instanceId"} and
        appId ^^ {(_: Webhook[Provision]).parameters.appId aka "parameters.appId"} and
        originInstanceId ^^ {(_: Webhook[Provision]).data.originInstanceId aka "data.originInstanceId"}
    }
  }


  "webhooks" should {
    "provision" in new ctx {
      callProvisionWebhook(aProvisionWebhook())
      there was after(timeout).one(mockFunc).apply(beSuccessfulTry[Webhook[Provision]].withValue(beProvisionWebhook(instanceId, appId)))
    }
  }

  step(shutdownEnv())
}
