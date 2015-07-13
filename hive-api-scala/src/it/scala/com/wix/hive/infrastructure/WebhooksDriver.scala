package com.wix.hive.infrastructure

import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.security.HiveRequestSigner
import com.wix.hive.server.webhooks._
import dispatch.{Req, url, _}
import org.jboss.netty.handler.codec.http._
import org.joda.time.format.ISODateTimeFormat
import com.wix.hive.json
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
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

  def callEmailSend(webhook: Webhook[EmailSend])

  def callSiteSettingsChanged(webhook: Webhook[SiteSettingsChanged])
  }

trait SimplicatorWebhooksDriver extends WebhooksDriver {
  val path: String
  val port: Int
  val secret: String

  val timeout = 5.seconds

  lazy val host = s"localhost:$port"
  lazy val signer = new HiveRequestSigner(secret)

  def aReq(instanceId: String, parameters: WebhookParameters, eventType: String, content: String): Req = {
    def getSignature(headers: Map[String, String], content: String): String = {
      val request = HttpRequestData(com.wix.hive.client.http.HttpMethod.POST, "", headers = headers, body = Some(content))
      signer.getSignature(request)
    }

    val headers = getHeaders(instanceId, parameters, eventType)

    val signatureHeader = "x-wix-signature" -> getSignature(headers, content)

    (url("http://" + host + path) << content <:< (headers + signatureHeader)).setMethod("POST")
  }

  protected def getHeaders(instanceId: String, parameters: WebhookParameters, eventType: String): Map[String, String] = {
    Map(
      "x-wix-application-id" -> parameters.appId,
      "x-wix-instance-id" -> instanceId,
      "x-wix-timestamp" -> ISODateTimeFormat.dateTime().print(parameters.timestamp),
      "x-wix-event-type" -> eventType,
      HttpHeaders.Names.HOST -> host)
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
    callWebhook(webhook, "/services/actions/done")
  }

  override def callEmailSend(webhook: Webhook[EmailSend]) = {
    callWebhook(webhook, "/services/actions/email/send")
  }

  override def callSiteSettingsChanged(webhook: Webhook[SiteSettingsChanged]) = {
    callWebhook(webhook, "/site/settings/changed")
  }

  protected def callWebhook(webhook: Webhook[_], eventType: String): Unit = {
    val payload = json.JacksonObjectMapper.mapper.writeValueAsString(webhook.data)
    val request = aReq(webhook.instanceId, webhook.parameters, eventType, payload)

    Await.ready(Http(request OK as.String), Duration(5, "seconds"))
  }
}