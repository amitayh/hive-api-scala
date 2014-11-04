package com.wix.hive.model.contacts

import org.joda.time.DateTime

case class Note(modifiedAt: Option[DateTime], content: String, id: Option[Int])
