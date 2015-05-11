package com.wix.hive.commands

import java.io.InputStream

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{HttpRequestData, NamedParameters}
import com.wix.hive.json.JacksonObjectMapper

import scala.reflect._

abstract class HiveCommand[T: ClassTag] {
  def url: String

  def method: HttpMethod

  def urlParams: String = ""

  def query: NamedParameters = Map.empty

  def headers: NamedParameters = Map.empty

  def body: Option[AnyRef] = None

  def createHttpRequestData: HttpRequestData = HttpRequestData(method, url + urlParams, query, headers, body)

  protected[commands] def mapValuesToStrings(params: Map[String, Any]): NamedParameters = params.collect {
    case (k, v: Some[_]) => k -> v.get.toString
    case (k, v: Seq[_]) if v.nonEmpty => k -> v.mkString(",")
    case (k, v: Enumeration#Value) => k -> v.toString
    case (k, v: String) => k -> v
  }

  def decode(r: InputStream): T = asT(r)

  def asT(r: InputStream): T = {
    val classOfR = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]

    if (classOf[scala.runtime.Nothing$] == classOfR || classOf[Unit] == classOfR) null.asInstanceOf[T]
    else JacksonObjectMapper.mapper.readValue(r, classOfR)
  }

}