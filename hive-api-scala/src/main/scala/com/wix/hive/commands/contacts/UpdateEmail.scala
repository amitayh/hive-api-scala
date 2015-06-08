package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateEmail(contactId: String, emailId: String, email: ContactEmailDTO, modifiedAt: DateTime) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/email/$emailId"

  override def body: Option[AnyRef] = Some(email)
}