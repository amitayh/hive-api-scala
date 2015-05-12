package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateEmail(contactId: String, emailId: String, email: ContactEmailDTO) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + s"/email/$emailId"

  override def body: Option[AnyRef] = Some(email)
}

object UpdateEmail {
  def apply(contactId: String, modifiedAt: DateTime, emailId: String, email: ContactEmailDTO): UpdateEmail =
    new UpdateEmail(contactId, emailId, email) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}