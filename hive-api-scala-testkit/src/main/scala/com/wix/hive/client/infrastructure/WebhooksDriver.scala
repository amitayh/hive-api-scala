package com.wix.hive.client.infrastructure

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import com.wix.hive.client.HiveSigner
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.server.webhooks._
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http._
import org.joda.time.format.ISODateTimeFormat
import com.twitter.util.{Duration => TwitterDuration}

import scala.concurrent.duration._

/**
 * User: maximn
 * Date: 12/2/14
 */
trait WebhooksDriver {
  def callProvisionWebhook(webhook: Webhook[Provision])

  def callProvisionDisabledWebhook(webhook: Webhook[ProvisionDisabled])

  def callActivityPosted(fromAppId: String, webhook: Webhook[ActivitiesPosted])

  def callServicesDone(webhook: Webhook[ServiceResult])
}

trait SimplicatorWebhooksDriver extends WebhooksDriver {
  val path: String
  val port: Int
  val secret: String

  val timeout = 5.seconds

  lazy val host = s"localhost:$port"
  lazy val client: Service[HttpRequest, HttpResponse] = Http.newService(host)
  lazy val signer = new HiveSigner(secret)

  def aReq(instanceId: String, parameters: WebhookParameters, eventType: String, content: String): HttpRequest = {
    def getSignature(headers: Map[String, String], content: String): String = {
      val request = HttpRequestData(com.wix.hive.client.http.HttpMethod.POST, "", headers = headers, body = Some(content))
      signer.getSignature(request)
    }

    val headers = Map(
      "x-wix-application-id" -> parameters.appId,
      "x-wix-instance-id" -> instanceId,
      "x-wix-timestamp" -> ISODateTimeFormat.dateTime().print(parameters.timestamp),
      "x-wix-event-type" -> eventType,
      HttpHeaders.Names.HOST -> host)

    val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path)
    req.setContent(ChannelBuffers.wrappedBuffer(content.getBytes("UTF8")))

    val signatureHeader = "x-wix-signature" -> getSignature(headers, content)

    (headers + signatureHeader) foreach { case (k: String, v: String) =>
      req.headers.add(k, v)
    }

    req
  }

  override def callProvisionWebhook(webhook: Webhook[Provision]) = {
    callWebhook(webhook, "/provision/provision")
  }

  override def callProvisionDisabledWebhook(webhook: Webhook[ProvisionDisabled]) = {
    callWebhook(webhook, "/provision/disabled")
  }

  override def callActivityPosted(fromAppId: String, webhook: Webhook[ActivitiesPosted]) = {
    callWebhook(webhook, "/activities/posted")
  }

  override def callServicesDone(webhook: Webhook[ServiceResult]) = {
    callWebhook(webhook, "/services/done")
  }

  private def callWebhook(webhook: Webhook[_], eventType: String) {
    val payload = JacksonObjectMapper.mapper.writeValueAsString(webhook.data)
    val request = aReq(webhook.instanceId, webhook.parameters, eventType, payload)

    Await.ready(client(request), TwitterDuration(timeout.length, timeout.unit))
  }
}