package com.wix.hive.webhooks

import com.wix.hive.drivers.WebhooksTestSupport
import com.wix.hive.infrastructure.SimplicatorWebhooksDriver
import com.wix.hive.server.webhooks.{Webhook, WebhookData}
import org.mockito.Mockito
import org.specs2.matcher.Matcher
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.specs2.specification.Scope

import scala.util.Try


/**
 * User: maximn
 * Date: 1/13/15
 */
class WebhooksIT extends SpecificationWithJUnit with WebhookSimplicatorIT {
  sequential

  trait ctx extends Scope
  with WebhooksTestSupport
  with SimplicatorWebhooksDriver {
    val path: String = webhookPath
    val secret: String = webhookSecret
    val port: Int = webhookPort

    val mockFunc = mock[Try[Webhook[_]] => Unit]

    subscribeFunc(mockFunc)

    def verifyWebhookWith[T <: WebhookData](matcher: Matcher[Webhook[T]]): Unit = {
      there was after(timeout).one(mockFunc).apply(beSuccessfulTry[Webhook[T]].withValue(matcher))
    }
  }

  "receive email send webhook" in new ctx {
    val webhook = anEmailSendWebhook
    callEmailSend(webhook)

    import webhook._

    verifyWebhookWith(beWebhook(instanceId, appId, beEmailSend(data.originId, data.correlationId, data.redemptionToken, data.contacts)))
  }

  "provision" in new ctx {
    callProvisionWebhook(aProvisionWebhook())

    verifyWebhookWith(beWebhook(instanceId, appId, beProvision(instanceId, beNone)))
  }

  "provision disabled" in new ctx {
    callProvisionDisabledWebhook(aProvisionDisabledWebhook())

    verifyWebhookWith(beWebhook(instanceId, appId, beProvisionDisabled(instanceId, beNone)))
  }

  "activity posted" in new ctx {
    callActivityPosted(appId, anActivityPostedWebhook())

    verifyWebhookWith(beWebhook(instanceId, appId, beActivity(anything, activityType)))
  }

  "services done" in new ctx {
    val doneWebhook = aServicesDoneWebhook()
    callServicesDone(doneWebhook)
    verifyWebhookWith(beWebhook(instanceId, appId, beServiceRunResult(doneWebhook.data.providerId, doneWebhook.data.correlationId, doneWebhook.data.data)))
  }

  "receive email send webhook" in new ctx {
    val webhook = anEmailSendWebhook
    callEmailSend(webhook)

    import webhook._
    verifyWebhookWith(beWebhook(instanceId, appId, beEmailSend(data.originId, data.correlationId, data.redemptionToken, data.contacts)))
  }
}
