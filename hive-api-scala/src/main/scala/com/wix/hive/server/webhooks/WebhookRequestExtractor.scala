package com.wix.hive.server.webhooks

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.webhooks.WebhooksConverter._
import org.joda.time.DateTime

import scala.util.Try

/**
 * User: maximn
 * Date: 4/7/15
 */
class WebhookRequestExtractor(secret: String) {
  protected lazy val validator = new WebhookSignatureVerification(secret)
  protected lazy val marshaller = new WebhooksMarshaller

  def convert(req: HttpRequestData): Try[Webhook[WebhookData]] = {
    val tryHeaderForReq = tryHeader(req, _: String)

    for {
      validRequest <- validator.verify(req)
      appId <- tryHeaderForReq(appIdKey)
      instanceId <- tryHeaderForReq(instanceIdKey)
      timestamp <- tryHeaderForReq(timestampKey)
      data <- marshaller.unmarshal(validRequest)
    } yield {
      val parameters = WebhookParameters(appId, new DateTime(timestamp))
      new Webhook(instanceId, data, parameters)
    }
  }
}
