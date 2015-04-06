package com.wix.hive.server

import com.wix.hive.drivers.{SigningTestSupport, WebhooksTestSupport}
import com.wix.hive.server.webhooks._
import com.wix.hive.server.webhooks.exceptions.MissingHeaderException
import org.specs2.matcher.Matchers
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

import scala.util.Try

/**
 * User: maximn
 * Date: 11/30/14
 */
class WebhooksConverterTest extends SpecificationWithJUnit with Matchers {

  trait ctx extends Scope
  with WebhooksTestSupport
  with SigningTestSupport {
    val processor = new WebhooksConverter(SigningTestSupport.key)
  }

  "process" should {
    "return InvalidWebhook for invalid signature" in new ctx {
      processor.convert(dataWithBody) must beFailedTry[Webhook[_]].withThrowable[MissingHeaderException]
    }

    "parse raw webhook to actual case class" in new ctx {
      processor.convert(provisioningWebhookRequest).asInstanceOf[Try[Webhook[Provision]]] must beSuccessfulTry(beWebhook[Provision](instance, app, beProvision(provisioningData.instanceId, provisioningData.originInstanceId)))
    }
  }
}