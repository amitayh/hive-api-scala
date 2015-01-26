package com.wix.hive.client

import java.util.UUID

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Duration
import com.wix.hive.drivers.WebhooksTestSupport
import com.wix.hive.infrastructure.WebhooksDriver
import com.wix.hive.server.webhooks._
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}
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
    override def onReq(webhook: Try[Webhook[_  <: WebhookData]]): Unit = mockFunc(webhook)
  }

  step(initEnv())


  trait ctx extends Before
  with HiveCommandsMatchers
  with WebhooksTestSupport {
    override def before: Any = beforeTest()

    val timeout = new org.specs2.time.Duration(2000)

    val client: Service[HttpRequest, HttpResponse] = Http.newService("localhost:8001")

    def aProvisionWebhook(instanceId: String = instanceId) = Webhook(instanceId, Provision(instanceId, None), aWebhookParams())

    def aProvisionDisabledWebhook(instanceId: String = instanceId) = Webhook(instanceId, ProvisionDisabled(instanceId, None), aWebhookParams())

    val activityId = "someActivityId"
    val activityType = "auth/register"

    def anActivityPostedWebhook(instanceId: String = instanceId) = Webhook(instanceId, ActivitiesPosted(activityId, activityType, None), aWebhookParams())
    def aServicesDoneWebhook(providerAppId: String = UUID.randomUUID().toString, instanceId: String = instanceId) = Webhook(instanceId, ServiceResult(providerAppId, "af142114-f616-4594-9fb8-1253d317541e", ServiceRunData("SUCCESS", None, None)), aWebhookParams(appId))

    def beActivity(activityId: Matcher[String], activityType: Matcher[String], contactId: Matcher[Option[String]] = beNone): Matcher[ActivitiesPosted] = {
      activityId ^^ {(_: ActivitiesPosted).activityId aka "activityId"} and
        activityType ^^ {(_: ActivitiesPosted).activityType aka "activityType"} and
        contactId ^^ {(_: ActivitiesPosted).contactId aka "contactId"}
    }


    def beProvisionDisabled(instanceId: Matcher[String], originInstanceId: Matcher[Option[String]] = beNone): Matcher[ProvisionDisabled] = {
      instanceId ^^ {(_: ProvisionDisabled).instanceId aka "instanceId"} and
        originInstanceId ^^ {(_: ProvisionDisabled).originInstanceId aka "originInstanceId"}
    }
  }


  "webhooks" should {
    "provision" in new ctx {
      callProvisionWebhook(aProvisionWebhook())
      there was after(timeout).one(mockFunc).apply(beSuccessfulTry[Webhook[Provision]].withValue(beWebhook(instanceId, appId, beProvision(instanceId, beNone))))
    }

    "provision disabled" in new ctx {
      callProvisionDisabledWebhook(aProvisionDisabledWebhook())

      there was after(timeout).one(mockFunc).apply(beSuccessfulTry[Webhook[ProvisionDisabled]].withValue(beWebhook(instanceId, appId, beProvisionDisabled(instanceId, beNone))))
    }

    "activity posted" in new ctx {
      callActivityPosted(appId, anActivityPostedWebhook())

      there was after(timeout).one(mockFunc).apply(beSuccessfulTry[Webhook[ActivitiesPosted]].withValue(beWebhook(instanceId, appId, beActivity(anything, activityType))))
    }

    "services done" in new ctx {
      val doneWebhook = aServicesDoneWebhook()
      callServicesDone(doneWebhook)
      there was after(timeout).one(mockFunc).apply(beSuccessfulTry[Webhook[ServiceResult]].withValue(beWebhook(instanceId, appId, beServiceRunResult())))
    }
  }

  step(shutdownEnv())
}