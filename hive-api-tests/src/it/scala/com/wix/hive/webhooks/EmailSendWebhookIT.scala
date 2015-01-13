package com.wix.hive.webhooks

import com.wix.hive.drivers.{WebhooksTestSupport, ServicesTestSupport}
import com.wix.hive.webhooks.BaseWebhookIT
import org.specs2.matcher.Matcher
import org.specs2.mutable.{SpecificationWithJUnit, Specification}
import org.specs2.specification.Scope


/**
 * User: maximn
 * Date: 1/13/15
 */
class EmailSendWebhookIT extends BaseWebhookIT {
  step {
    this
  }

  trait ctx extends Scope
  with WebhooksTestSupport
  with ServicesTestSupport {

  }

  step {
    srv.start()
  }

  "receive email send webhook" in new ctx {
    val webhook = anEmailSendWebhook
    callEmailSend(webhook)

    import webhook._
    webhookWith(beWebhook(instanceId, appId, beEmailSend(data.originId, data.correlationId, data.redemptionToken, data.contacts)))
  }

  step {
    srv.stop()
  }
}
