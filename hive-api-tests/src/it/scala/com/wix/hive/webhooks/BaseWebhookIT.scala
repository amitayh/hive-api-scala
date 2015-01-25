package com.wix.hive.webhooks

import java.util.UUID

import com.wix.hive.client.{FinagleWebhooksWebServer, WebhooksTestkit}
import com.wix.hive.server.webhooks.{Webhook, WebhookData}
import org.specs2.matcher.Matcher
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationWithJUnit

import scala.util.Try

/**
 * User: maximn
 * Date: 1/13/15
 */
class BaseWebhookIT extends SpecificationWithJUnit
with WebhooksTestkit
with Mockito {
  override val path: String = "localhost/webhook-url/"
  override val secret: String = UUID.randomUUID().toString
  override val port: Int = 8001

  val mockFunc = mock[Try[Webhook[_]] => Unit]

  val srv = new FinagleWebhooksWebServer(port, secret) {
    override def onReq(webhook: Try[Webhook[_  <: WebhookData]]): Unit = mockFunc(webhook)
  }

  def webhookWith[T <: WebhookData](matcher: Matcher[Webhook[T]]): Unit ={
    there was after(timeout).one(mockFunc).apply(beSuccessfulTry[Webhook[T]].withValue(matcher))
  }
}
