package com.wix.hive.model.insights

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.activities.ActivityType.ActivityType
import com.wix.hive.model.activities.ActivityTypeRef
import org.joda.time.DateTime

case class ActivityTypesSummary(@JsonScalaEnumeration(classOf[ActivityTypeRef])activityType: Option[ActivityType],
                                 total: Int,
                                 from: DateTime,
                                 until: Option[DateTime] = None)
