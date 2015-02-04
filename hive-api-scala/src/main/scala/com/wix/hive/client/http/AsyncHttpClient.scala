package com.wix.hive.client.http

import java.util.concurrent.ExecutionException

import com.ning.http.client.Response
import com.wix.hive.client.http.DispatchHttpClient.`2XX`
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.model.WixAPIErrorException
import dispatch.{Http, url, _}

import scala.concurrent.ExecutionContextExecutor
import scala.reflect.{ClassTag, _}


trait AsyncHttpClient {
  def request[T: ClassTag](data: HttpRequestData): Future[T]
}

class DispatchHttpClient()(implicit val executionContext: ExecutionContextExecutor) extends AsyncHttpClient {
  override def request[T: ClassTag](data: HttpRequestData): Future[T] = {
    val postDataAsString: String = data.bodyAsString

    val req = (url(data.url) << postDataAsString <<? data.queryString <:< data.headers).setMethod(data.method.toString)

    Http(req > handle[T] _)(executionContext).recover {
      case e: ExecutionException => throw e.getCause
    }
  }


  def handle[T: ClassTag](r: Response): T = {
    r.getStatusCode match {
      case `2XX`() => asT[T](r)
      case 404 => throw WixAPIErrorException(r.getStatusCode, Some(r.getStatusText))
      case _ => throw JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOf[WixAPIErrorException])
    }
  }

  def asT[T: ClassTag](r: Response): T = {
    val classOfT = classTag.runtimeClass.asInstanceOf[Class[T]]

    if (classOf[scala.runtime.Nothing$] == classOfT || classOf[Unit] == classOfT) null.asInstanceOf[T]
    else JacksonObjectMapper.mapper.readValue(r.getResponseBodyAsStream, classOfT)
  }
}

object DispatchHttpClient {

  private object `2XX` {
    def unapply(code: Int): Boolean = code / 100 == 2
  }

}


case class HttpRequestData(method: HttpMethod,
                           url: String,
                           queryString: NamedParameters = Map.empty,
                           headers: NamedParameters = Map.empty,
                           body: Option[AnyRef] = None)

object HttpRequestDataImplicits {

  implicit class HttpRequestDataStringify(val data: HttpRequestData) extends AnyVal {
    def bodyAsString: String = data.body match {
      case Some(body: String) => body
      case Some(body: AnyRef) => JacksonObjectMapper.mapper.writeValueAsString(body)
      case None => ""
    }
  }

}

object HttpMethod extends Enumeration {
  type HttpMethod = Value
  val GET, POST, PUT, DELETE = Value
}