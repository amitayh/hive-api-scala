package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.Activity

case class GetActivityById[TResponse](id: String) extends HiveBaseCommand[Activity] {
  override def method: HttpMethod = HttpMethod.GET

  override val url: String = "/activities"

  override def urlParams = s"/$id"
}