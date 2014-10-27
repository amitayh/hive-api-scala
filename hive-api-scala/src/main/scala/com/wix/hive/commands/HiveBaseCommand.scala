package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{NamedParameters, HttpRequestData}


trait HiveBaseCommand[T]{
  def url : String
  def method : HttpMethod

  def urlParams: String = ""
  def query : NamedParameters = Map() //TODO: -> Map.empty
  def headers: NamedParameters = Map()
  def body: Option[AnyRef] = None

  def createHttpRequestData : HttpRequestData = HttpRequestData(method ,url + urlParams, query, headers, body)
}