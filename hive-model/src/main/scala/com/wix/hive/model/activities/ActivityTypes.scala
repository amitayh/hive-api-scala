package com.wix.hive.model.activities

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.activities.ActivityType.ActivityType

case class ActivityTypes(@JsonScalaEnumeration(classOf[ActivityTypeRef]) types: Seq[ActivityType])
