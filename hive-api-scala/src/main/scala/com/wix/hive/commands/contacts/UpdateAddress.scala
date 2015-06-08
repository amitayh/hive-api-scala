package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateAddress(contactId: String, modifiedAt: DateTime, addressId: String,address: AddressDTO) extends UpdateContactCommand[Contact] {
  override def urlParams: String = super.urlParams + s"/address/$addressId"

  override def body: Option[AnyRef] = Some(address)
}