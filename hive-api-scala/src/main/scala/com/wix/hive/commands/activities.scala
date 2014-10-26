package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.model.Activity

abstract class ActivityCommand[TResponse] extends HiveBaseCommand[TResponse] {
  override def url: String = "/activities"
}

case class GetActivityById(id: String) extends ActivityCommand[Activity] {
  override def method: HttpMethod = HttpMethod.GET

  override def urlParams = s"/$id"
}

case class GetActivityTypes() extends ActivityCommand[Seq[String]] {
  override def method: HttpMethod = GET
}