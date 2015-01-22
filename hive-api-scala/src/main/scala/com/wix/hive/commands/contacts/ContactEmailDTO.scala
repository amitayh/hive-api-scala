package com.wix.hive.commands.contacts

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.contacts.EmailStatus.EmailStatus
import com.wix.hive.model.contacts.EmailStatusRef

case class ContactEmailDTO(tag: String, email: String, @JsonScalaEnumeration(classOf[EmailStatusRef]) emailStatus: Option[EmailStatus])
