package com.wix.hive.model

import com.wix.hive.model.ActivityType.ActivityType
import org.joda.time.DateTime

case class ActivitySummary(activityTypes: Seq[ActivityTypesSummary], total: Int, from: DateTime, until: DateTime)

case class ActivityTypesSummary(activityType: Option[ActivityType], total: Int, from: DateTime, until: DateTime)
