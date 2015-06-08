package com.wix.hive.commands.contacts

import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.wix.hive.model.contacts.EmailStatus.EmailStatus
import com.wix.hive.model.contacts.{Contact, EmailStatusRef}
import org.joda.time.DateTime

case class UpdateEmailSubscription(contactId: String, emailId: String, subscription: EmailStatus, modifiedAt: DateTime) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/email/$emailId/subscription"

  override def body: Option[AnyRef] = Some(ContactResult(subscription))
}

case class ContactResult(@JsonScalaEnumeration(classOf[EmailStatusRef]) status: EmailStatus)