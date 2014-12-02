package com.wix.hive.model.contacts

import org.joda.time.DateTime

case class ImportantDate(tag: String, date: DateTime, id: Option[Int])
case class ActivityImportantDate(tag: String, date: DateTime)
