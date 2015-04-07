package com.wix.hive.server.webhooks

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.adapters.RequestConverterFrom

import scala.util.Try

/**
 * User: maximn
 * Date: 12/1/14
 */
object WebhooksConverter {
  val appIdKey = "x-wix-application-id"
  val instanceIdKey = "x-wix-instance-id"
  val timestampKey = "x-wix-timestamp"
}

trait GenericWebhooksConverter[+W <: WebhookBase[WebhookData]] {
  protected def secret: String

  protected val webhookRequestExtractor = new WebhookRequestExtractor(secret)

  def convert[T: RequestConverterFrom](originalReq: T): Try[W] = {
    val req = implicitly[RequestConverterFrom[T]].convert(originalReq)
    convert(req)
  }

  protected def convert(req: HttpRequestData): Try[W]
}

class WebhooksConverter(protected val secret: String) extends GenericWebhooksConverter[Webhook[WebhookData]] {
  protected def convert(req: HttpRequestData): Try[Webhook[WebhookData]] = {
    webhookRequestExtractor.convert(req)
  }
}

