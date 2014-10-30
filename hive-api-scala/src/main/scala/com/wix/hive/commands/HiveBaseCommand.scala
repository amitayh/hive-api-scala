package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{HttpRequestData, NamedParameters}


trait HiveBaseCommand[T]{
  def url: String
  def method: HttpMethod

  def urlParams: String = ""
  def query: NamedParameters = Map.empty
  def headers: NamedParameters = Map.empty
  def body: Option[AnyRef] = None

  final def createHttpRequestData: HttpRequestData = HttpRequestData(method, url + urlParams, query, headers, body)
}