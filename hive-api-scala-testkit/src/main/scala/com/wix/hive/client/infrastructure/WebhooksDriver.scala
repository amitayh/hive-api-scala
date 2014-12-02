package com.wix.hive.client.infrastructure

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.server.webhooks.Provision
import org.jboss.netty.buffer.{ChannelBuffers, ChannelBuffer}
import org.jboss.netty.handler.codec.http._
import org.joda.time.DateTime


/**
 * User: maximn
 * Date: 12/2/14
 */
trait WebhooksDriver {
  def callProvisionWebhook(appDef: AppDef, provision: Provision)
  def verifyProvisionWebhook(appDef: AppDef, provision: Provision) = {

  }
}

trait SimplicatorWebhooksDriver extends WebhooksDriver {
  val url: String
  val port: Int
  val client: Service[HttpRequest, HttpResponse] = Http.newService(s"$url:$port")

  def aReq(appDef: AppDef, eventType: String, content: String): HttpRequest = {
    val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url)
    req.setContent(ChannelBuffers.wrappedBuffer(content.getBytes("UTF8")))

    req
  }


  def callProvisionWebhook(appDef: AppDef, provision: Provision) = {
    val payload = JacksonObjectMapper.mapper.writeValueAsString(provision)
    val resp = client(aReq(appDef, "/provision/provision", payload))

    Await.ready(resp)
  }
}