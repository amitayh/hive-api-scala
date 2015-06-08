package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.{ContactName, Contact}
import org.joda.time.DateTime

case class UpdatePhone(contactId: String, phoneId: String, phone: ContactPhoneDTO, modifiedAt: DateTime) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/phone/$phoneId"

  override def body: Option[AnyRef] = Some(phone)
}