package com.wix.hive.model.notifications

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.notifications.NotificationType.NotificationType
import org.joda.time.DateTime

class NotificationCreationData(title: String, content: String,
                               @JsonScalaEnumeration(classOf[NotificationTypeRef]) `type`: NotificationType,
                               clickToAction: Option[ClickToAction] = None,
                               expiration: Option[DateTime] = None)
