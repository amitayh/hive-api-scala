package com.wix.hive.server

import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Duration, Future}
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.adapters.Finagle.RequestConverterFromFinagle
import com.wix.hive.server.adapters.RequestConverterFrom
import com.wix.hive.server.webhooks.{Webhook, WebhookData, WebhooksConverter}
import org.jboss.netty.handler.codec.http._

import scala.util.Try

/**
 * User: maximn
 * Date: 11/27/14
 */
trait WebServerBase {
  def start(): ListeningServer

  def stop(after: Duration): Future[Unit]

  protected def process[T: RequestConverterFrom](data: T): Unit
}

abstract class FinagleWebServer(port: Int) extends WebServerBase {
  val serviceDefinition = new Service[HttpRequest, HttpResponse] {

    def apply(req: HttpRequest): Future[HttpResponse] = {
      process(req)
      Future.value(new DefaultHttpResponse(req.getProtocolVersion, HttpResponseStatus.OK))
    }
  }

  override protected def process[T: RequestConverterFrom](data: T): Unit = {
    val httpReqData = implicitly[RequestConverterFrom[T]].convert(data)
    process(httpReqData)
  }

  def process(data: HttpRequestData): Unit

  private lazy val httpServer = Http.serve(s":$port", serviceDefinition)

  def start(): ListeningServer = httpServer

  def stop(after: Duration = Duration.fromSeconds(1)): Future[Unit] = httpServer.close(after)
}


abstract class FinagleWebhooksWebServer(val port: Int, val secret: String) extends FinagleWebServer(port) with WebhooksConverter {

   def process(request: HttpRequestData): Unit = {
    val webhook = this.convert(request)
    onReq(webhook)
  }

  def onReq(webhook: Try[Webhook[_ <: WebhookData]]): Unit
}

