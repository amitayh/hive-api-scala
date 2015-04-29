package com.wix.hive.client.http

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.json.JacksonObjectMapper
import dispatch._

import scala.reflect.ClassTag

trait AsyncHttpClient {
  def request[T: ClassTag](data: HttpRequestData): Future[T]
}

case class HttpRequestData(method: HttpMethod,
                           url: String,
                           queryString: NamedParameters = Map.empty,
                           headers: NamedParameters = Map.empty,
                           body: Option[AnyRef] = None)

object HttpRequestDataImplicits {

  implicit class HttpRequestDataStringify(val data: HttpRequestData) extends AnyVal {

    def bodyAsString: String = data.body.fold("") {
      case s: String => s
      case other: AnyRef => JacksonObjectMapper.mapper.writeValueAsString(other)
    }
  }

}

object HttpMethod extends Enumeration {
  type HttpMethod = Value
  val GET, POST, PUT, DELETE = Value
}