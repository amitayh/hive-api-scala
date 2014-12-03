package com.wix.hive.client.infrastructure

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import com.wix.hive.client.HiveSigner
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.server.providers.FinagleProvider.finagleReq2myReq
import com.wix.hive.server.webhooks.{Webhook, WebhookParameters}
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http._

/**
 * User: maximn
 * Date: 12/2/14
 */
trait WebhooksDriver {
  def callProvisionWebhook(webhook: Webhook)
}

trait SimplicatorWebhooksDriver extends WebhooksDriver {
  val url: String
  val port: Int
  val secret: String
  val host: String = url.takeWhile(_ != '/')
  val client: Service[HttpRequest, HttpResponse] = Http.newService(s"$host:$port")

  private val signer = new HiveSigner(secret)

  def aReq(instanceId: String, parameters: WebhookParameters, eventType: String, content: String): HttpRequest = {
    val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url)
    req.setContent(ChannelBuffers.wrappedBuffer(content.getBytes("UTF8")))
    req.headers.add("X-Wix-Application-Id", parameters.appId)
    req.headers.add("X-Wix-Instance-Id", instanceId)
    req.headers.add("X-Wix-Timestamp", parameters.timestamp)
    req.headers.add("X-Wix-Event-Type", eventType)

    req.headers.add("X-Wix-Signature", signer.getSignature(req))

    req
  }


  def callProvisionWebhook(webhook: Webhook) = {
    val payload = JacksonObjectMapper.mapper.writeValueAsString(webhook.data)
    val resp = client(aReq(webhook.instanceId, webhook.parameters, "/provision/provision", payload))

  Await.ready(resp)
}
}