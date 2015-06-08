package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddEmail(contactId: String, email: ContactEmailDTO, modifiedAt: DateTime) extends AddToContactCommand[Contact] {
  override val urlParams: String = super.urlParams + "/email"

  override val body: Option[AnyRef] = Some(email)
}