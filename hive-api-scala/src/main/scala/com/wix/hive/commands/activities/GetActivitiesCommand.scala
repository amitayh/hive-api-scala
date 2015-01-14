package com.wix.hive.commands.activities

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{HttpMethod, NamedParameters}
import com.wix.hive.commands.HiveCommand
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.commands.common.PageSizes.PageSizes
import com.wix.hive.model.activities.ActivityScope
import com.wix.hive.model.activities.ActivityScope.ActivityScope
import org.joda.time.DateTime

abstract class GetActivitiesCommand(activityTypes: Seq[String] = Nil,
                                        until: Option[DateTime] = None,
                                        from: Option[DateTime] = None,
                                        scope: ActivityScope = ActivityScope.site,
                                        cursor: Option[String] = None,
                                        pageSize: PageSizes = PageSizes.`25`) extends HiveCommand[PagingActivitiesResult] {
  override val method: HttpMethod = HttpMethod.GET

  override def query: NamedParameters =
    super.mapValuesToStrings(Map(
      GetActivitiesCommand.ScopeKey -> scope,
      GetActivitiesCommand.PageSizeKey -> pageSize,
      GetActivitiesCommand.ActivityTypesKey -> activityTypes,
      GetActivitiesCommand.UntilKey -> until,
      GetActivitiesCommand.FromKey -> from,
      GetActivitiesCommand.CursorKey -> cursor))
}

object GetActivitiesCommand {
  val ActivityTypesKey = "activityTypes"
  val UntilKey = "until"
  val FromKey = "from"
  val ScopeKey = "scope"
  val CursorKey = "cursor"
  val PageSizeKey = "pageSize"
}