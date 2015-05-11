package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime


case class AddPhone(contactId: String, phone: ContactPhoneDTO) extends AddToContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override val urlParams: String = super.urlParams + "/phone"

  override val body: Option[AnyRef] = Some(phone)
}

object AddPhone {
  def apply(contactId: String, modifiedAt: DateTime, phone: ContactPhoneDTO): AddPhone =
    new AddPhone(contactId, phone) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}

