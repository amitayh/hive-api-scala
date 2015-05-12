package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class AddEmail(contactId: String, email: ContactEmailDTO) extends AddToContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override val urlParams: String = super.urlParams + "/email"

  override val body: Option[AnyRef] = Some(email)
}

object AddEmail {
  def apply(contactId: String, modifiedAt: DateTime, email: ContactEmailDTO): AddEmail = new AddEmail(contactId, email) {
    override val modifiedAtOption: Option[DateTime] = Some(modifiedAt)
  }
}
