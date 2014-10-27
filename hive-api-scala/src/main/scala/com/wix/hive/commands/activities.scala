package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.client.http.NamedParameters
import com.wix.hive.commands.ActivityScope.ActivityScope
import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.contacts.PageSizes
import com.wix.hive.commands.contacts.PageSizes.PageSizes
import com.wix.hive.commands.contacts.PageSizes.PageSizes
import com.wix.hive.model.{CreateActivity, ActivityCreatedResult, ActivityTypes, Activity}
import org.joda.time.DateTime

import scalaz.Alpha.Q

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

case class GetActivities(activityTypes: Seq[String] = Nil, until: Option[DateTime] = None, from: Option[DateTime] = None,
                         scope: ActivityScope = ActivityScope.site, cursor: Option[String] = None, pageSize: PageSizes = PageSizes.`25`)
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
    Map(QueryKeys.activityTypes -> activityTypes.mkString(","),
      QueryKeys.until -> (if (until.isDefined) until.get.toString else ""),
      QueryKeys.from -> (if (from.isDefined) from.get.toString else ""),
      QueryKeys.scope -> scope.toString,
      QueryKeys.cursor -> (if (cursor.isDefined) cursor.get.toString else ""),
      QueryKeys.pageSize -> pageSize.toString).
      filter { case (k, v) => v.nonEmpty}
  }
}

object ActivityScope extends Enumeration {
  type ActivityScope = Value
  val site, app = Value
}

case class PagingActivitiesResult(pageSize: Int, previousCursor: Option[String], nextCursor: Option[String], results: Seq[Activity])