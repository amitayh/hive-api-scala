package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.model.{NotificationCreationData, NotificationResult}

case class CreateNotification(notificationData: NotificationCreationData) extends HiveBaseCommand[NotificationResult] {
  override val url: String = "/notifications"

  override val method: HttpMethod = HttpMethod.POST
}