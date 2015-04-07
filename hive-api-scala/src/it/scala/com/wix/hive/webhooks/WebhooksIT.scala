package com.wix.hive.webhooks

import com.wix.hive.drivers.WebhooksTestSupport._
import com.wix.hive.infrastructure.{SimplicatorWebhooksDriver, WebhookSimplicatorIT, WebhookITBase}


class WebhooksIT extends WebhookITBase with WebhookSimplicatorIT {
  class webhooksCtx extends ctx with SimplicatorWebhooksDriver

  "Webhooks" should {
    "receive email send webhook" in new webhooksCtx {
      val webhook = anEmailSendWebhook
      callEmailSend(webhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beEmailSend(webhook.data.originId, webhook.data.correlationId, webhook.data.redemptionToken, webhook.data.contacts)))
    }

    "provision" in new webhooksCtx {
      callProvisionWebhook(aProvisionWebhook())

      verifyWebhookWith(beWebhook(instanceId, appId, beProvision(instanceId, beNone)))
    }

    "provision disabled" in new webhooksCtx {
      callProvisionDisabledWebhook(aProvisionDisabledWebhook())

      verifyWebhookWith(beWebhook(instanceId, appId, beProvisionDisabled(instanceId, beNone)))
    }

    "activity posted" in new webhooksCtx {
      callActivityPosted(appId, anActivityPostedWebhook())

      verifyWebhookWith(beWebhook(instanceId, appId, beActivity(anything, activityType)))
    }

    "services done" in new webhooksCtx {
      val doneWebhook = aServicesDoneWebhook()
      callServicesDone(doneWebhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beServiceRunResult(doneWebhook.data.providerId, doneWebhook.data.correlationId, doneWebhook.data.data)))
    }

    "receive email send webhook" in new webhooksCtx {
      val webhook = anEmailSendWebhook
      callEmailSend(webhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beEmailSend(webhook.data.originId, webhook.data.correlationId, webhook.data.redemptionToken, webhook.data.contacts)))
    }
  }
}