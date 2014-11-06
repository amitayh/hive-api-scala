package com.wix.hive.commands.activities

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.{HttpMethod, NamedParameters}
import com.wix.hive.commands.HiveBaseCommand
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.commands.common.PageSizes.PageSizes
import com.wix.hive.model.activities.ActivityScope
import com.wix.hive.model.activities.ActivityScope.ActivityScope
import org.joda.time.DateTime

abstract class BaseGetActivitiesCommand(activityTypes: Seq[String] = Nil,
                                        until: Option[DateTime] = None,
                                        from: Option[DateTime] = None,
                                        scope: ActivityScope = ActivityScope.site,
                                        cursor: Option[String] = None,
                                        pageSize: PageSizes = PageSizes.`25`) extends HiveBaseCommand[PagingActivitiesResult] {
  override val method: HttpMethod = HttpMethod.GET

  override def query: NamedParameters =
    super.mapToStrings(Map(
      BaseGetActivitiesCommand.ScopeKey -> scope,
      BaseGetActivitiesCommand.PageSizeKey -> pageSize,
      BaseGetActivitiesCommand.ActivityTypesKey -> activityTypes,
      BaseGetActivitiesCommand.UntilKey -> until,
      BaseGetActivitiesCommand.FromKey -> from,
      BaseGetActivitiesCommand.CursorKey -> cursor))
}

object BaseGetActivitiesCommand {
  val ActivityTypesKey = "activityTypes"
  val UntilKey = "until"
  val FromKey = "from"
  val ScopeKey = "scope"
  val CursorKey = "cursor"
  val PageSizeKey = "pageSize"
}