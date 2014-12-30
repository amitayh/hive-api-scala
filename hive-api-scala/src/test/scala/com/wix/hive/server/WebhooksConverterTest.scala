package com.wix.hive.server

import com.wix.hive.drivers.SigningTestSupport
import com.wix.hive.server.webhooks.exceptions.MissingHeaderException
import com.wix.hive.server.webhooks.{Webhook, WebhooksConverter}
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

/**
 * User: maximn
 * Date: 11/30/14
 */
class WebhooksConverterTest extends SpecificationWithJUnit {

  trait ctx extends Scope
  with SigningTestSupport{
    val processor = new WebhooksConverter{
      override val secret: String = key
    }
  }

  "process" should {
    "return InvalidWebhook for invalid signature" in new ctx {
      processor.convert(dataWithBody) must beFailedTry[Webhook[_]].withThrowable[MissingHeaderException]
    }

    "parse raw webhook to actual case class" in new ctx {
      processor.convert(provisioningWebhookRequest) must beSuccessfulTry(provisioningWebhook)
    }
  }
}