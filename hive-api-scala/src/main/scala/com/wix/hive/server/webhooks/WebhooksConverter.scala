package com.wix.hive.server.webhooks

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.webhooks.WebhooksConverter._
import org.joda.time.DateTime

import scala.util.Try
/**
 * User: maximn
 * Date: 12/1/14
 */
object WebhooksConverter {
  val appIdKey = "X-Wix-Application-Id"
  val instanceIdKey = "X-Wix-Instance-Id"
  val timestampKey = "X-Wix-Timestamp"
}

trait  WebhooksConverter extends HttpRequestHelpers {
  def secret: String
  private lazy val validator = new WebhookSignatureVerification(secret)
  private lazy val marshaller = new WebhooksMarshaller

  def convert[T <% HttpRequestData](req: T): Try[Webhook] = {
    val tryHeaderForReq = tryHeader(req, _: String)

    for {
      validRequest <- validator.verify(req)
      appId <- tryHeaderForReq(appIdKey)
      instanceId <- tryHeaderForReq(instanceIdKey)
      timestamp <- tryHeaderForReq(timestampKey)
      data <- marshaller.unmarshal(validRequest)
    } yield {
      val parameters = WebhookParameters(appId, new DateTime(timestamp))
      Webhook(instanceId, data, parameters)
    }
  }
}