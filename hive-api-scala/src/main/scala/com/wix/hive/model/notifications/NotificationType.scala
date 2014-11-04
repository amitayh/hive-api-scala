package com.wix.hive.model.notifications

object NotificationType extends Enumeration {
  type NotificationType = Value
  val NewFeatures = Value("New Features")
  val SpecialOffer = Value("Special Offers")
  val BusinessTips = Value("Business Tips")
}
