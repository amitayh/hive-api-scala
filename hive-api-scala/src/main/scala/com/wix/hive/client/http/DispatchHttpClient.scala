package com.wix.hive.client.http

import java.util.concurrent.ExecutionException

import com.ning.http.client.Response
import com.wix.hive.client.http.DispatchHttpClient.`2XX`
import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.model.WixAPIErrorException
import dispatch.{url, _}

import scala.concurrent.ExecutionContextExecutor
import scala.reflect.{ClassTag, _}
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
  override def request[T: ClassTag](data: HttpRequestData): Future[T] = {
    val postDataAsString: String = data.bodyAsString

    val req = (url(data.url) << postDataAsString <<? data.queryString <:< data.headers)
      .setMethod(data.method.toString)
      .setBodyEncoding("UTF-8")

    Http(req > handle[T] _)(executionContext).recover {
      case e: ExecutionException => throw e.getCause
    }(executionContext)
  }

  def handle[T: ClassTag](r: Response): T = {
      r.getStatusCode match {
        case `2XX`() => asT[T](r)
        case 404 => {
          Try { JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOf[WixAPIErrorException]) } match {
            case Failure(_) => throw WixAPIErrorException(r.getStatusCode, Some(r.getStatusText))
            case Success(e) => throw e
          }
        }
        case _ => throw JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOf[WixAPIErrorException])
      }
  }

  def asT[T: ClassTag](r: Response): T = {
    val classOfT = classTag.runtimeClass.asInstanceOf[Class[T]]

    if (classOf[scala.runtime.Nothing$] == classOfT || classOf[Unit] == classOfT) null.asInstanceOf[T]
    else JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOfT)
  }
}