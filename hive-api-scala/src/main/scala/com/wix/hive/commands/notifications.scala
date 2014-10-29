package com.wix.hive.commands

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.model.{NotificationResult, NotificationCreationData}

case class CreateNotification(notificationData: NotificationCreationData) extends HiveBaseCommand[NotificationResult] {
  override def url: String = "/notifications"

  override def method: HttpMethod = HttpMethod.POST
}