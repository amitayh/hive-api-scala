package com.wix.hive.model

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.ActivityType.ActivityType
import org.joda.time.DateTime

case class ActivitySummary(activityTypes: Seq[ActivityTypesSummary], total: Int, from: DateTime, until: Option[DateTime] = None)

case class ActivityTypesSummary(@JsonScalaEnumeration(classOf[ActivityTypeRef])activityType: Option[ActivityType],
                                total: Int,
                                from: DateTime,
                                until: Option[DateTime] = None)
