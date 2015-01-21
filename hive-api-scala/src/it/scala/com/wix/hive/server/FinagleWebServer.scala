package com.wix.hive.server

import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Duration, Future}
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.adapters.RequestConverterFrom
import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpRequest, HttpResponse, HttpResponseStatus}
import com.wix.hive.server.adapters.Finagle.RequestConverterFromFinagle

/**
 * User: maximn
 * Date: 1/21/15
 */
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
