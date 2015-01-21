package com.wix.hive.server

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.webhooks.{Webhook, WebhookData, WebhooksConverter}

import scala.util.Try

/**
 * User: maximn
 * Date: 1/21/15
 */
abstract class FinagleWebhooksWebServer(val port: Int, val secret: String) extends FinagleWebServer(port) with WebhooksConverter {

   def process(request: HttpRequestData): Unit = {
    val webhook = this.convert(request)
    onReq(webhook)
  }

  def onReq(webhook: Try[Webhook[_ <: WebhookData]]): Unit
}
