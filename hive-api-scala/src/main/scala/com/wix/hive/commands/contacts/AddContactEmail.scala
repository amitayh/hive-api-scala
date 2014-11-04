package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddContactEmail(contactId: String, modifiedAt: DateTime, email: ContactEmailDTO) extends AddToContactCommand[Contact] {
  override def urlParams: String = super.urlParams + "/email"

  override def body: Option[AnyRef] = Some(email)
}
