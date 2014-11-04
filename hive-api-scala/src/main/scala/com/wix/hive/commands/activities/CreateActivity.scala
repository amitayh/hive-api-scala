package com.wix.hive.commands.activities

import com.wix.hive.client.http.HttpMethod.{HttpMethod, _}
import com.wix.hive.client.http._
import com.wix.hive.model.activities.{ActivityCreatedResult, ActivityCreationData}

case class CreateActivity(userSessionToken: String, activity: ActivityCreationData) extends ActivityCommand[ActivityCreatedResult] {
  private val userSessionTokenKey: String = "userSessionToken"

  override val method: HttpMethod = POST

  override val body: Option[AnyRef] = Some(activity)

  override val query: NamedParameters = Map(userSessionTokenKey -> userSessionToken)
}
