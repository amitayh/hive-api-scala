package com.wix.hive.webhooks

import com.wix.hive.drivers.WebhooksTestSupport
import com.wix.hive.infrastructure.{SimplicatorWebhooksDriver, WebhookSimplicatorIT}
import com.wix.hive.server.webhooks.{Webhook, WebhookData}
import org.specs2.matcher.{Matcher, ThrownExpectations}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.After

import scala.util.Try


/**
 * User: maximn
 * Date: 1/13/15
 */
class WebhooksIT extends SpecificationWithJUnit with WebhookSimplicatorIT
with SimplicatorWebhooksDriver
with WebhooksTestSupport {
  sequential

  val path: String = webhookPath
  val secret: String = webhookSecret
  val port: Int = webhookPort

  class ctx extends After {
    val mockFunc = mock[Try[Webhook[_]] => Unit]
    subscribeFunc(mockFunc)

    override def after: Any = clearListener()

    def verifyWebhookWith[T <: WebhookData](matcher: Matcher[Webhook[T]]): Unit = {
      eventually {
        there was one(mockFunc).apply(beSuccessfulTry[Webhook[T]].withValue(matcher))
      }
    }
  }

  "Webhooks" should {
    "receive email send webhook" in new ctx {
      val webhook = anEmailSendWebhook
      callEmailSend(webhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beEmailSend(webhook.data.originId, webhook.data.correlationId, webhook.data.redemptionToken, webhook.data.contacts)))
    }
//
//    "provision" in new ctx {
//      callProvisionWebhook(aProvisionWebhook())
//
//      verifyWebhookWith(beWebhook(instanceId, appId, beProvision(instanceId, beNone)))
//    }
//
//    "provision disabled" in new ctx {
//      callProvisionDisabledWebhook(aProvisionDisabledWebhook())
//
//      verifyWebhookWith(beWebhook(instanceId, appId, beProvisionDisabled(instanceId, beNone)))
//    }
//
//    "activity posted" in new ctx {
//      callActivityPosted(appId, anActivityPostedWebhook())
//
//      verifyWebhookWith(beWebhook(instanceId, appId, beActivity(anything, activityType)))
//    }
//
//    "services done" in new ctx {
//      val doneWebhook = aServicesDoneWebhook()
//      callServicesDone(doneWebhook)
//      verifyWebhookWith(beWebhook(instanceId, appId, beServiceRunResult(doneWebhook.data.providerId, doneWebhook.data.correlationId, doneWebhook.data.data)))
//    }
//
//    "receive email send webhook" in new ctx {
//      val webhook = anEmailSendWebhook
//      callEmailSend(webhook)
//
//      verifyWebhookWith(beWebhook(instanceId, appId, beEmailSend(webhook.data.originId, webhook.data.correlationId, webhook.data.redemptionToken, webhook.data.contacts)))
//    }
  }
}
