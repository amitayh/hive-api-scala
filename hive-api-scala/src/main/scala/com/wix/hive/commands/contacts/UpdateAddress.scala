package com.wix.hive.commands.contacts

import com.wix.hive.model.contacts.Contact
import org.joda.time.DateTime

case class UpdateAddress(contactId: String, addressId: String,address: AddressDTO) extends UpdateContactCommand[Contact] {
  override val modifiedAtOption: Option[DateTime] = None

  override def urlParams: String = super.urlParams + s"/address/$addressId"

  override def body: Option[AnyRef] = Some(address)
}

object UpdateAddress {
  def apply(contactId: String, modifiedAt: DateTime, addressId: String, address: AddressDTO): UpdateAddress =
    new UpdateAddress(contactId, addressId, address) { override val modifiedAtOption: Option[DateTime] = Some(modifiedAt) }
}
