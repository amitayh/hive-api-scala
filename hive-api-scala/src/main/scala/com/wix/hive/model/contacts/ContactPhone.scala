package com.wix.hive.model.contacts

case class ContactPhone(tag: String, phone: String, normalizedPhone: Option[String] = None, id: Option[Int] = None)
case class ActivityContactPhone(tag: String, phone: String)
