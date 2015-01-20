package com.wix.hive.model.contacts

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.contacts.EmailStatus._

case class ContactEmail(tag: String, email: String, @JsonScalaEnumeration(classOf[EmailStatusRef]) emailStatus: EmailStatus, id: Option[Int] = None)
case class ActivityContactEmail(tag: String, email: String)
