package com.wix.hive.model.activities

import org.joda.time.DateTime

case class ActivityCreationData(createdAt: DateTime, activityLocationUrl: Option[String] = None, activityDetails: Option[ActivityDetails] = None,
                                activityInfo: ActivityInfo, contactUpdate: Option[ContactActivity] = None) {
  val activityType = activityInfo.activityType.toString
}
