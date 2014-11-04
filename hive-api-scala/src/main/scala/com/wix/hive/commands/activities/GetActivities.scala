package com.wix.hive.commands.activities

import com.wix.hive.client.http.HttpMethod.{HttpMethod, _}
import com.wix.hive.client.http._
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.commands.common.PageSizes._
import com.wix.hive.model.activities.ActivityScope
import com.wix.hive.model.activities.ActivityScope.ActivityScope
import org.joda.time.DateTime

case class GetActivities(activityTypes: Seq[String] = Nil,
                         until: Option[DateTime] = None,
                         from: Option[DateTime] = None,
                         scope: ActivityScope = ActivityScope.site,
                         cursor: Option[String] = None,
                         pageSize: PageSizes = PageSizes.`25`)
  extends ActivityCommand[PagingActivitiesResult] {

  override val method: HttpMethod = GET

  override def query: NamedParameters =
    super.removeOptionalParameters(Map(
      GetActivities.ScopeKey -> scope,
      GetActivities.PageSizeKey -> pageSize,
      GetActivities.ActivityTypesKey -> activityTypes,
      GetActivities.UntilKey -> until,
      GetActivities.FromKey -> from,
      GetActivities.CursorKey -> cursor))
}

object GetActivities {
  val ActivityTypesKey = "activityTypes"
  val UntilKey = "until"
  val FromKey = "from"
  val ScopeKey = "scope"
  val CursorKey = "cursor"
  val PageSizeKey = "pageSize"
}



