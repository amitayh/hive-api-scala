package com.wix.hive.commands.contacts

import com.wix.hive.model.Contact
import org.joda.time.DateTime

case class AddContactAddress(contactId: String, modifiedAt: DateTime, address: AddressDTO) extends AddToContactCommand[Contact] {
  override def urlParams: String = super.urlParams + "/address"

  override def body: Option[AnyRef] = Some(address)
}
