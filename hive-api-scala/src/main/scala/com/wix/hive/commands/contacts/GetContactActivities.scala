package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.activities.{BaseGetActivitiesCommand, PagingActivitiesResult}
import com.wix.hive.commands.common.PageSizes
import com.wix.hive.commands.common.PageSizes._
import com.wix.hive.model.activities.ActivityScope
import com.wix.hive.model.activities.ActivityScope._
import com.wix.hive.model.activities.ActivityType.ActivityType
import org.joda.time.DateTime

case class GetContactActivities(contactId: String,
                                activityTypes: Seq[String] = Nil,
                                until: Option[DateTime] = None,
                                from: Option[DateTime] = None,
                                scope: ActivityScope = ActivityScope.site,
                                cursor: Option[String] = None,
                                pageSize: PageSizes = PageSizes.`25`) extends BaseGetActivitiesCommand(activityTypes, until, from, scope, cursor, pageSize) {
  override val url: String = "/contacts"
  override val urlParams = s"/$contactId/activities"
}