package com.wix.hive.webhooks

import com.wix.hive.drivers.WebhooksTestSupport
import com.wix.hive.infrastructure.SimplicatorWebhooksDriver
import org.mockito.Mockito
import org.specs2.mutable.{Before, SpecificationWithJUnit}


/**
 * User: maximn
 * Date: 1/13/15
 */
class WebhooksIT extends SpecificationWithJUnit with WebhookSimplicatorIT {
  sequential

  trait ctx extends Before
  with WebhooksTestSupport
  with SimplicatorWebhooksDriver {

    override def before: Any = Mockito.reset(mockFunc)

    val path: String = webhookPath
    val secret: String = webhookSecret
    val port: Int = webhookPort
  }

  "receive email send webhook" in new ctx {
    val webhook = anEmailSendWebhook
    callEmailSend(webhook)

    import webhook._

    webhookWith(beWebhook(instanceId, appId, beEmailSend(data.originId, data.correlationId, data.redemptionToken, data.contacts)))
  }
}
