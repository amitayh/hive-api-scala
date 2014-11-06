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
  extends BaseGetActivitiesCommand(activityTypes, until, from, scope, cursor, pageSize){
  override val url: String = "/activities"
}