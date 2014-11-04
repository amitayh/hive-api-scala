package com.wix.hive.commands.notifications

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.HiveBaseCommand
import com.wix.hive.model.notifications.{NotificationCreationData, NotificationResult}

case class CreateNotification(notificationData: NotificationCreationData) extends HiveBaseCommand[NotificationResult] {
  override val url: String = "/notifications"

  override val method: HttpMethod = HttpMethod.POST
}
