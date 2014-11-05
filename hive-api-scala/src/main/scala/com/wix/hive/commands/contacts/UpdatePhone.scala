package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdatePhone(contactId: String, modifiedAt: DateTime, phoneId: String, phone: ContactPhoneDTO) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/phone/$phoneId"

  override def body: Option[AnyRef] = Some(phone)
}
