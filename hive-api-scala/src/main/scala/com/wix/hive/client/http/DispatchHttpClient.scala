package com.wix.hive.client.http

import java.io.InputStream
import java.util.concurrent.ExecutionException

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.ning.http.client.Response
import com.wix.hive.client.http.DispatchHttpClient.`2XX`
import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.model.WixAPIErrorException
import dispatch.{url, _}

import scala.concurrent.ExecutionContextExecutor
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

class DispatchHttpClient()(implicit val executionContext: ExecutionContextExecutor) extends AsyncHttpClient {
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
    try {
      r.getStatusCode match {
        case `2XX`() => r.getResponseBodyAsStream
        case 404 => {
          Try { JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOf[WixAPIErrorException]) } match {
            case Failure(_) => throw WixAPIErrorException(r.getStatusCode, Some(r.getStatusText))
            case Success(e) => throw e
          }
        }
        case _ => throw JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOf[WixAPIErrorException])
      }
    } catch {
      case e@(_: JsonParseException | _: JsonMappingException) => throw new WixAPIErrorException(r.getStatusCode, Some(s"Couldn't parse response because of ${e.getClass.getName}: ${r.getResponseBody}"))
    }
  }
}