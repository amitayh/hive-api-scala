package com.wix.hive.commands.notifications

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod._
import com.wix.hive.commands.HiveCommand
import com.wix.hive.model.notifications.{NotificationCreationData, NotificationResult}

case class CreateNotification(notificationData: NotificationCreationData) extends HiveCommand[NotificationResult] {
  override val url: String = "/notifications"

  override val method: HttpMethod = HttpMethod.POST
}
