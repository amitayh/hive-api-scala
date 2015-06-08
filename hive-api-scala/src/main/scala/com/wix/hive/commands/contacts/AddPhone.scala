package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime


case class AddPhone(contactId: String, modifiedAt: DateTime, phone: ContactPhoneDTO) extends AddToContactCommand[Contact] {
  override val urlParams: String = super.urlParams + "/phone"

  override val body: Option[AnyRef] = Some(phone)
}

