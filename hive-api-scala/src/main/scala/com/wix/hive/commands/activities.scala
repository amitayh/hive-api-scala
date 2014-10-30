package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.NamedParameters
import com.wix.hive.commands.ActivityScope.ActivityScope
import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.contacts.PageSizes.PageSizes
import com.wix.hive.commands.contacts.PageSizes.PageSizes
import com.wix.hive.model.ActivityTypes
import com.wix.hive.model.{Activity, ActivityCreatedResult, ActivityTypes, ActivityCreationData}
import org.joda.time.DateTime
import com.wix.hive.commands.GetActivities._

abstract class ActivityCommand[TResponse] extends HiveBaseCommand[TResponse] {
  override val url: String = "/activities"
}

case class GetActivityById(id: String) extends ActivityCommand[Activity] {
  override val method: HttpMethod = GET

  override val urlParams = s"/$id"
}

case class GetActivityTypes() extends ActivityCommand[ActivityTypes] {
  override val urlParams: String = "/types"

  override val method: HttpMethod = GET
}

case class CreateActivity(userSessionToken: String, activity: ActivityCreationData) extends ActivityCommand[ActivityCreatedResult] {
  private val userSessionTokenKey: String = "userSessionToken"

  override val method: HttpMethod = POST

  override val body: Option[AnyRef] = Some(activity)

  override val query: NamedParameters = Map(userSessionTokenKey -> userSessionToken)
}

case class GetActivities(activityTypes: Seq[String] = Nil,
                         until: Option[DateTime] = None,
                         from: Option[DateTime] = None,
                         scope: ActivityScope = ActivityScope.default,
                         cursor: Option[String] = None,
                         pageSize: PageSizes = PageSizes.default)
  extends ActivityCommand[PagingActivitiesResult] {

  override val method: HttpMethod = GET

  override val query: NamedParameters = Map(
    ScopeKey -> scope,
    PageSizeKey -> pageSize,
    ActivityTypesKey -> activityTypes,
    UntilKey -> until,
    FromKey -> from,
    CursorKey -> cursor)
    .collect {
    case (k, v: Some[_]) => k -> v.get.toString
    case (k, v: Seq[_]) if v.nonEmpty => k -> v.mkString(",")
    case (k, v: Enumeration#Value) => k -> v.toString
  }
}

object GetActivities {
  val ActivityTypesKey = "activityTypes"
  val UntilKey = "until"
  val FromKey = "from"
  val ScopeKey = "scope"
  val CursorKey = "cursor"
  val PageSizeKey = "pageSize"
}

object ActivityScope extends Enumeration {
  type ActivityScope = Value
  val site, app = Value

  val default = site
}

case class PagingActivitiesResult(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[Activity]) {

  def previousPageCommand: Option[GetActivities] = previousCursor.map(_ => GetActivities(cursor = this.previousCursor))

  def nextPageCommand: Option[GetActivities] = nextCursor.map(_ => GetActivities(cursor = this.nextCursor))
}