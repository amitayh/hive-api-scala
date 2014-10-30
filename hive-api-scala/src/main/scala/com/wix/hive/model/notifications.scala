package com.wix.hive.model

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.NotificationType.NotificationType
import org.joda.time.DateTime


class NotificationCreationData(title: String, content: String,
                               @JsonScalaEnumeration(classOf[NotificationTypeRef]) `type`: NotificationType,
                               clickToAction: Option[ClickToAction] = None,
                               expiration: Option[DateTime] = None)

class NotificationTypeRef extends TypeReference[NotificationType.type]
object NotificationType extends Enumeration {
  type NotificationType = Value
  val NewFeatures = Value("New Features")
  val SpecialOffer = Value("Special Offers")
  val BusinessTips = Value("Business Tips")
}

case class ClickToAction(label: Option[String], url: Option[String])

case class NotificationResult()