package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.{ContactName, Contact}
import org.joda.time.DateTime

case class UpdatePhone(contactId: String, phoneId: String, phone: ContactPhoneDTO) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + s"/phone/$phoneId"

  override def body: Option[AnyRef] = Some(phone)
}

object UpdatePhone {
  def apply(contactId: String, modifiedAt: DateTime, phoneId: String, phone: ContactPhoneDTO): UpdatePhone =
    new UpdatePhone(contactId, phoneId, phone) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}