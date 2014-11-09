package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{HttpRequestData, NamedParameters}


trait HiveBaseCommand[T] {
  def url: String

  def method: HttpMethod

  def urlParams: String = ""

  def query: NamedParameters = Map.empty

  def headers: NamedParameters = Map.empty

  def body: Option[AnyRef] = None

  def createHttpRequestData: HttpRequestData = HttpRequestData(method, url + urlParams, query, headers, body)

  protected def mapValuesToStrings(params: Map[String, Any]): NamedParameters = params.collect {
    case (k, v: Some[_]) => k -> v.get.toString
    case (k, v: Seq[_]) if v.nonEmpty => k -> v.mkString(",")
    case (k, v: Enumeration#Value) => k -> v.toString
    case (k, v: String) => k -> v
  }
}