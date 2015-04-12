package com.wix.hive.model.insights

import org.joda.time.DateTime

case class ActivitySummary(activityTypes: Seq[ActivityTypesSummary], total: Int, from: DateTime, until: Option[DateTime] = None)
