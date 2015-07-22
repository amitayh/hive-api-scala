package com.wix.hive.webhooks

import com.wix.hive.drivers.WebhooksTestSupport._
import com.wix.hive.infrastructure.{SimplicatorWebhooksDriver, WebhookITBase, WebhookSimplicatorIT}

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

    "receive site settings changed webhook" in new webhooksCtx {
      val webhook = aSiteSettingsChangedWebhook

      callSiteSettingsChanged(webhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beEqualTo(webhook.data)))
    }

    "receive contact created webhook" in new webhooksCtx {
      val webhook = aContactCreatedWebhook

      callContactCreated(webhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beEqualTo(webhook.data)))
    }

    "receive contact updated webhook" in new webhooksCtx {
      val webhook = aContactUpdatedWebhook

      callContactUpdated(webhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beEqualTo(webhook.data)))
    }

    "receive contact deleted webhook" in new webhooksCtx {
      val webhook = aContactDeletedWebhook

      callContactDeleted(webhook)

      verifyWebhookWith(beWebhook(instanceId, appId, beEqualTo(webhook.data)))
    }
 }
}