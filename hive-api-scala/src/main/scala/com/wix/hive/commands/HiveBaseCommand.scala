package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.client.http.http.NamedParameters


trait HiveBaseCommand[T]{
  //def validate : accord.Result
  def url : String
  def method : HttpMethod

  def urlParams: String = ""
  def query : NamedParameters = Map()
  def headers: NamedParameters = Map()
  def body: Option[AnyRef] = None

  def createHttpRequestData : HttpRequestData = HttpRequestData(method ,url + urlParams, query, headers, body)
}

//object HiveBaseCommand {
//  implicit val hiveBaseCommandValidator = validator[HiveBaseCommand[_]] {x => ()}
//}
