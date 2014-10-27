package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.NamedParameters
import com.wix.hive.model.{CreateActivity, ActivityCreatedResult, ActivityTypes, Activity}

abstract class ActivityCommand[TResponse] extends HiveBaseCommand[TResponse] {
  override def url: String = "/activities"
}

case class GetActivityById(id: String) extends ActivityCommand[Activity] {
  override def method: HttpMethod = GET

  override def urlParams = s"/$id"
}

case class GetActivityTypes() extends ActivityCommand[ActivityTypes] {
  override def url: String = super.url + "/types"

  override def method: HttpMethod = GET
}

case class PostActivity(userSessionToken: String, activity: CreateActivity) extends ActivityCommand[ActivityCreatedResult] {
  private val userSessionTokenKey: String = "userSessionToken"

  override def method: HttpMethod = POST

  override def body: Option[AnyRef] = Some(activity)

  override def query: NamedParameters = Map(userSessionTokenKey -> userSessionToken)
}