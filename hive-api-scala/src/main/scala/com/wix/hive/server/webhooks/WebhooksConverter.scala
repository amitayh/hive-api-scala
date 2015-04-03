package com.wix.hive.server.webhooks

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.adapters.RequestConverterFrom
import com.wix.hive.server.webhooks.WebhooksConverter._
import org.joda.time.DateTime

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

//TODO: why trait and not class? Why extends HttpReqHelpers and not import?
trait WebhooksConverter extends HttpRequestHelpers {
  def secret: String

  private lazy val validator = new WebhookSignatureVerification(secret)
  private lazy val marshaller = new WebhooksMarshaller

  def convert[T: RequestConverterFrom](originalReq: T): Try[Webhook[WebhookData]] = {
    val req = implicitly[RequestConverterFrom[T]].convert(originalReq)
    convert(req)
  }

  def convert(req: HttpRequestData): Try[Webhook[WebhookData]] = {
    val tryHeaderForReq = tryHeader(req, _: String)
    for {
      validRequest <- validator.verify(req)
      appId <- tryHeaderForReq(appIdKey)
      instanceId <- tryHeaderForReq(instanceIdKey)
      timestamp <- tryHeaderForReq(timestampKey)
      data <- marshaller.unmarshal(validRequest)
    } yield {
      val parameters = GenericWebhookParameters(appId, new DateTime(timestamp))
      new Webhook(instanceId, data, parameters)
        with OriginalRequestStorage {override def request: HttpRequestData = req}
    }
  }
}

trait OriginalRequestStorage {
  def request: HttpRequestData
}