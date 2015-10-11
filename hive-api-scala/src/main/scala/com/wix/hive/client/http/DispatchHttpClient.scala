package com.wix.hive.client.http

import java.io.InputStream
import java.util.concurrent.ExecutionException

import com.ning.http.client.Response
import com.wix.hive.client.http.DispatchHttpClient.`2XX`
import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.model.WixAPIErrorException
import dispatch.{url, _}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

/**
 * User: maximn
 * Date: 4/29/15
 */
object DispatchHttpClient {

  private object `2XX` {
    def unapply(code: Int): Boolean = code / 100 == 2
  }

}

class DispatchHttpClient()(implicit val executionContext: ExecutionContext) extends AsyncHttpClient {
  override def request(data: HttpRequestData): Future[InputStream] = {
    val postDataAsString: String = data.bodyAsString

    val req = (url(data.url) << postDataAsString <<? data.queryString <:< data.headers)
      .setMethod(data.method.toString)
      .setBodyEncoding("UTF-8")

    Http(req > handle _)(executionContext).recover {
      case e: ExecutionException => throw e.getCause
    }(executionContext)
  }

  def handle(r: Response): InputStream = {
      r.getStatusCode match {
        case `2XX`() => r.getResponseBodyAsStream
        case _ => {
          Try { JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOf[WixAPIErrorException]) } match {
            case Failure(_) => throw WixAPIErrorException(r.getStatusCode, Some(r.getStatusText))
            case Success(e) => throw e
          }
        }
      }
  }
}