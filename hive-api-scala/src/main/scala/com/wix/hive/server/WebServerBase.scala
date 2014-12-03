package com.wix.hive.server

import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Duration, Future}
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.providers.FinagleProvider.finagleReq2myReq
import com.wix.hive.server.webhooks.{Webhook, WebhooksConverter}
import org.jboss.netty.handler.codec.http._

import scala.util.Try

/**
 * User: maximn
 * Date: 11/27/14
 */
trait WebServerBase  {
  def start(): ListeningServer
  def stop(after: Duration): Future[Unit]

}

abstract class FinagleWebServer(port: Int) extends WebServerBase with ReqeustProcessor {
  val serviceDefinition = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] = {
      val d: HttpRequestData = req
      process(d)
      Future.value(new DefaultHttpResponse(req.getProtocolVersion, HttpResponseStatus.OK))
    }
  }

  private lazy val httpServer = Http.serve(s":$port", serviceDefinition)

  def start(): ListeningServer = httpServer

  def stop(after: Duration): Future[Unit] = httpServer.close(after)
}


abstract class FinagleWebhooksWebServer(port: Int) extends FinagleWebServer(port) with WebhooksConverter {

  override def process[T <% HttpRequestData](req: HttpRequestData): Unit =
  {
    val webhook = this.convert(req)
    onReq(webhook)
  }

  def onReq(webhook: Try[Webhook]): Unit
}

