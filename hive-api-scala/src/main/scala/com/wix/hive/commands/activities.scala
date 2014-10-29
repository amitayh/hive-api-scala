package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.NamedParameters
import com.wix.hive.commands.ActivityScope.ActivityScope
import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.contacts.PageSizes.PageSizes
import com.wix.hive.commands.contacts.PageSizes.PageSizes
import com.wix.hive.model.{Activity, ActivityCreatedResult, ActivityTypes, ActivityCreationData}
import org.joda.time.DateTime

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

case class CreateActivity(userSessionToken: String, activity: ActivityCreationData) extends ActivityCommand[ActivityCreatedResult] {
  private val userSessionTokenKey: String = "userSessionToken"

  override def method: HttpMethod = POST

  override def body: Option[AnyRef] = Some(activity)

  override def query: NamedParameters = Map(userSessionTokenKey -> userSessionToken)
}

case class GetActivities(activityTypes: Seq[String] = Nil, until: Option[DateTime] = None, from: Option[DateTime] = None,
                         scope: ActivityScope = ActivityScope.default, cursor: Option[String] = None, pageSize: PageSizes = PageSizes.default)
  extends ActivityCommand[PagingActivitiesResult] {
  override def method: HttpMethod = GET

  private object QueryKeys {
    val activityTypes = "activityTypes"
    val until = "until"
    val from = "from"
    val scope = "scope"
    val cursor = "cursor"
    val pageSize = "pageSize"
  }

  override def query: NamedParameters = {
    val requiredParams = Map(QueryKeys.scope -> scope.toString,
      QueryKeys.pageSize -> pageSize.toString)

    val optionalParams = Map(QueryKeys.activityTypes -> activityTypes,
      QueryKeys.until -> until,
      QueryKeys.from -> from,
      QueryKeys.cursor -> cursor)
      .collect {
      case (k, v: Some[_]) => k -> v.get.toString
      case (k, v: Seq[_]) if v.nonEmpty => k -> v.mkString(",")
    }

    (requiredParams ++ optionalParams).toMap
  }
}

object ActivityScope extends Enumeration {
  type ActivityScope = Value
  val site, app = Value

  val default = site
}

case class PagingActivitiesResult(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[Activity]) {
  def previousPageCommand = nextCursor.map(c => GetActivities(cursor = this.previousCursor))

  def nextPageCommand = nextCursor.map(c => GetActivities(cursor = this.nextCursor))
}